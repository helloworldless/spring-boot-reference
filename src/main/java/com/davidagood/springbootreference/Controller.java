package com.davidagood.springbootreference;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Instant;
import java.util.function.Supplier;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class Controller {

	private final RestTemplate restTemplate;

	private final SecretWordsClient secretWordsClient;

	private final HeadersMapper headersMapper;

	private final Supplier<Instant> timestampSupplier;

	@GetMapping("/headers")
	public HeadersDto getHeaders() {
		log.info("Getting headers");
		RequestEntity<Void> requestEntity = RequestEntity.get(URI.create("https://httpbin.org/headers")).build();
		log.info("Making HTTP request method={}, url={}", requestEntity.getMethod(), requestEntity.getUrl());
		ResponseEntity<Headers> responseEntity = restTemplate.exchange(requestEntity, Headers.class);
		Headers headers = responseEntity.getBody();
		return headersMapper.map(headers, timestampSupplier.get());
	}

	@GetMapping("/words")
	public SecretWordsDto getSecretWords() {
		log.info("Getting secret words");
		return new SecretWordsDto(secretWordsClient.getSecretWords(), timestampSupplier.get());
	}

}
