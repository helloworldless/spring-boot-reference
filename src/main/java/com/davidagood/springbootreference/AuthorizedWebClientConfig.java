package com.davidagood.springbootreference;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AuthorizedWebClientConfig {

	@Bean
	WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager, ExchangeStrategies exchangeStrategies) {
		ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client = new ServletOAuth2AuthorizedClientExchangeFilterFunction(
				authorizedClientManager);
		// @formatter:off
		return WebClient.builder()
						.apply(oauth2Client.oauth2Configuration())
						.exchangeStrategies(exchangeStrategies)
						.build();
		// @formatter:on
	}

	@Bean
	ExchangeStrategies exchangeStrategies(ObjectMapper objectMapper) {
		Jackson2JsonEncoder encoder = new Jackson2JsonEncoder(objectMapper);
		Jackson2JsonDecoder decoder = new Jackson2JsonDecoder(objectMapper);
		return ExchangeStrategies.builder().codecs(configurer -> {
			configurer.defaultCodecs().jackson2JsonEncoder(encoder);
			configurer.defaultCodecs().jackson2JsonDecoder(decoder);
		}).build();
	}

	@Bean
	OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
			OAuth2AuthorizedClientRepository authorizedClientRepository,
			OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> tokenResponseClient) {

		OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
				.clientCredentials(r -> r.accessTokenResponseClient(tokenResponseClient)).clientCredentials().build();

		DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
				clientRegistrationRepository, authorizedClientRepository);
		authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

		return authorizedClientManager;
	}

	@Bean
	OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> tokenResponseClient() {
		return new DefaultClientCredentialsTokenResponseClient();
	}

	@Bean
	InMemoryClientRegistrationRepository clientRegistrationRepository() {
		return new InMemoryClientRegistrationRepository(myClientRegistration());
	}

	@Bean
	ClientRegistration myClientRegistration() {
		// @formatter:off
		return ClientRegistration
				.withRegistrationId("my-client")
				.clientId("dummy-client-id")
				.clientSecret("dummy-client-secret")
				.clientAuthenticationMethod(ClientAuthenticationMethod.POST)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.scope("*")
				.tokenUri("https://dummy-token-uri/token")
				.build();
		// @formatter:on
	}

}
