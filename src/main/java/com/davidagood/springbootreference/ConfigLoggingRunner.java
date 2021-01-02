package com.davidagood.springbootreference;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConfigLoggingRunner implements CommandLineRunner {

	private final AppConfig appConfig;

	@Override
	public void run(String... args) {
		log.info("{}", appConfig);
	}

}
