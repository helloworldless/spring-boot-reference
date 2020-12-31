package com.davidagood.springbootreference;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.function.Supplier;

@SpringBootApplication
public class SpringBootApp {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootApp.class, args);
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	HeadersMapper headersMapper() {
		return HeadersMapper.INSTANCE;
	}

	@Bean
	Supplier<Instant> timestampSupplier() {
		return Instant::now;
	}

}
