package com.davidagood.springbootreference;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizedWebClientIT {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	@Qualifier("oauth2RestTemplate")
	RestTemplate oauth2RestTemplate;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	ClientRegistration clientRegistration;

	private MockRestServiceServer mockServer;

	@BeforeEach
	void setUp() {
		this.mockServer = MockRestServiceServer.createServer(oauth2RestTemplate);
	}

	@Test
	void happyPath() throws Exception {
		mockServer.expect(once(), requestTo("https://dummy-token-uri/token"))
				.andExpect(content()
						.contentType(new MediaType(MediaType.APPLICATION_FORM_URLENCODED, StandardCharsets.UTF_8)))
				.andExpect(content().formData(createGrantRequestFormData()))
				.andRespond(withSuccess(createTokenResponseBody(), MediaType.APPLICATION_JSON));

		mockMvc.perform(get("/api/words")).andExpect(status().isOk()).andExpect(
				MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(List.of("speakers", "keyboard"))));

		// TODO - Stub the HTTP request in SecretWordsClient#getSecretWords

		mockServer.verify();
	}

	@SneakyThrows
	private String createTokenResponseBody() {
		return objectMapper.writeValueAsString(createTokenResponse());
	}

	private Map<String, Object> createTokenResponse() {
		// @formatter:off
		return Map.of(
				OAuth2ParameterNames.ACCESS_TOKEN, "dummy-access-token",
				OAuth2ParameterNames.EXPIRES_IN, 3600,
				OAuth2ParameterNames.REFRESH_TOKEN, "dummy-refresh-token",
				OAuth2ParameterNames.TOKEN_TYPE, OAuth2AccessToken.TokenType.BEARER.getValue()
		);
		// @formatter:on
	}

	/*
	 * Reusing some of the code in Spring OAuth's
	 * DefaultClientCredentialsTokenResponseClient.getTokenResponse
	 */
	@SuppressWarnings("unchecked")
	private MultiValueMap<String, String> createGrantRequestFormData() {
		var grantRequest = new OAuth2ClientCredentialsGrantRequest(clientRegistration);
		RequestEntity<?> requestEntity = new OAuth2ClientCredentialsGrantRequestEntityConverter().convert(grantRequest);
		return (MultiValueMap<String, String>) requestEntity.getBody();
	}

	@TestConfiguration
	static class TestConfig {

		@Bean("oauth2RestTemplate")
		RestTemplate oauth2RestTemplate() {
			RestTemplate restTemplate = new RestTemplate(
					Arrays.asList(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
			restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
			return restTemplate;
		}

		@Bean
		OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> tokenResponseClient(
				@Qualifier("oauth2RestTemplate") RestTemplate oauth2RestTemplate) {
			var defaultTokenResponseClient = new DefaultClientCredentialsTokenResponseClient();
			defaultTokenResponseClient.setRestOperations(oauth2RestTemplate);
			return defaultTokenResponseClient;
		}

	}

}
