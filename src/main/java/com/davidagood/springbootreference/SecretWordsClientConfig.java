package com.davidagood.springbootreference;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties("secret-words-client")
@ConstructorBinding
@Value
@NonFinal
@Validated
public class SecretWordsClientConfig {

	@NotBlank
	@URL
	String url;

}
