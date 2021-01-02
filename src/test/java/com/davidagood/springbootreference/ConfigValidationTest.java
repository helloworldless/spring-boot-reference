package com.davidagood.springbootreference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = { ValidationAutoConfiguration.class })
class ConfigValidationTest {

	@Autowired
	LocalValidatorFactoryBean localValidatorFactoryBean;

	AppConfig expectedAppConfig = new AppConfig("test-user", 4, new AppConfig.UserApi("http://fake-url"));

	ConfigurationPropertiesBinderValidator<AppConfig> binderValidator;

	String delimiter = ".";

	String app = "app";

	String userPath = Path.from(delimiter, app, "user").toString();

	String threadsPath = Path.from(delimiter, app, "threads").toString();

	String userApiUrlPath = Path.from(delimiter, app, "user-api", "url").toString();

	@BeforeEach
	void setUp() {
		binderValidator = ConfigurationPropertiesBinderValidator.forClass(AppConfig.class)
				.propertiesFile("application.yml").prefix(app).validator(localValidatorFactoryBean);
	}

	@Test
	void validationSuccess() throws BindException {
		AppConfig appConfig = binderValidator.build();
		assertThat(appConfig).isEqualTo(expectedAppConfig);
	}

	@Test
	void userMissing() {
		var appConfigBinderValidator = binderValidator.omitProperty(userPath);
		assertThrows(BindException.class, appConfigBinderValidator::build);
	}

	@Test
	void userEmpty() {
		var appConfigBinderValidator = binderValidator.propertyOverride(userPath, "");
		assertThrows(BindException.class, appConfigBinderValidator::build);
	}

	@Test
	void threadsMissing() {
		var appConfigBinderValidator = binderValidator.omitProperty(threadsPath);
		assertThrows(BindException.class, appConfigBinderValidator::build);
	}

	@Test
	void threadsEmpty() {
		var appConfigBinderValidator = binderValidator.propertyOverride(threadsPath, "");
		assertThrows(BindException.class, appConfigBinderValidator::build);
	}

	@Test
	void threadsInvalid() {
		var appConfigBinderValidator = binderValidator.propertyOverride(threadsPath, "-1");
		assertThrows(BindException.class, appConfigBinderValidator::build);
	}

	@Test
	void urlMissing() {
		var appConfigBinderValidator = binderValidator.omitProperty(userApiUrlPath);
		assertThrows(BindException.class, appConfigBinderValidator::build);
	}

	@Test
	void urlEmpty() {
		var appConfigBinderValidator = binderValidator.propertyOverride(userApiUrlPath, "");
		assertThrows(BindException.class, appConfigBinderValidator::build);
	}

	@Test
	void urlInvalid() {
		var appConfigBinderValidator = binderValidator.propertyOverride(userApiUrlPath, "htp://typo.com");
		assertThrows(BindException.class, appConfigBinderValidator::build);
	}

}
