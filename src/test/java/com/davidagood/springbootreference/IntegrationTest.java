package com.davidagood.springbootreference;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.function.Supplier;

import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTest {

	static final Instant TIMESTAMP = LocalDate.of(2020, 12, 31).atStartOfDay().atZone(ZoneId.of("America/New_York"))
			.toInstant();

	@Autowired
	MockMvc mockMvc;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ObjectMapper objectMapper;

	@TestConfiguration
	static class TestConfig {

		@Bean
		Supplier<Instant> timestampSupplier() {
			return () -> TIMESTAMP;
		}

	}

	@Test
	void happyPath() throws Exception {
		var headers = new Headers();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		headers.add(HttpHeaders.USER_AGENT, "Dummy User Agent");

		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);

		// .andExpect(MockRestRequestMatchers.content().json(""))
		mockServer.expect(once(), requestTo("https://httpbin.org/headers"))
				.andRespond(withSuccess(objectMapper.writeValueAsBytes(headers), MediaType.APPLICATION_JSON));

		HeadersDto expected = HeadersDto.builder().headers(headers.getHeaders()).createdAt(TIMESTAMP).build();

		mockMvc.perform(get("/api/headers")).andDo(print()).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expected), true));
	}

}
