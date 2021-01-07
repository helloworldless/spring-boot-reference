package com.davidagood.springbootreference;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecretWordsClient {

	private final WebClient webClient;

	public List<String> getSecretWords() {
		var get = HttpMethod.GET;
		var url = "https://dummy-secret-words-resource-server/api/words";
		log.info("Making HTTP request method={}, url={}", get, url);
		// @formatter:off
		return webClient.method(get)
				.uri(url)
                .attributes(clientRegistrationId("my-client"))
                .retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();
        // @formatter:on
	}

}
