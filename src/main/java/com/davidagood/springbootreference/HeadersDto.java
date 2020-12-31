package com.davidagood.springbootreference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

@Value
@Builder
@AllArgsConstructor
public class HeadersDto {

	@NonNull
	Map<String, String> headers;

	@NonNull
	@Builder.Default
	Instant createdAt = Instant.now();

}
