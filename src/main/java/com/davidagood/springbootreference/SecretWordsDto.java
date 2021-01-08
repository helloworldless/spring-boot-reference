package com.davidagood.springbootreference;

import lombok.NonNull;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
public class SecretWordsDto {

	@NonNull
	List<String> words;

	@NonNull
	Instant createdAt;

}
