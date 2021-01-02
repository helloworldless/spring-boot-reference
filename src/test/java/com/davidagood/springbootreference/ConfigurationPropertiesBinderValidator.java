package com.davidagood.springbootreference;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

// Based on https://tuhrig.de/testing-configurationproperties-in-spring-boot/
public class ConfigurationPropertiesBinderValidator<T> {

	private final Class<T> targetClass;

	private String fileName;

	private String prefix;

	private Validator validator;

	private final Properties propertyOverrides = new Properties();

	private final List<String> propertiesToRemove = new ArrayList<>();

	private ConfigurationPropertiesBinderValidator(Class<T> targetClass) {
		this.targetClass = targetClass;
	}

	public static <T> ConfigurationPropertiesBinderValidator<T> forClass(Class<T> targetClass) {
		return new ConfigurationPropertiesBinderValidator<>(targetClass);
	}

	public ConfigurationPropertiesBinderValidator<T> propertiesFile(String fileName) {
		this.fileName = fileName;
		return this;
	}

	public ConfigurationPropertiesBinderValidator<T> prefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public ConfigurationPropertiesBinderValidator<T> validator(Validator validator) {
		this.validator = validator;
		return this;
	}

	public ConfigurationPropertiesBinderValidator<T> propertyOverride(String key, String value) {
		propertyOverrides.setProperty(key, value);
		return this;
	}

	public ConfigurationPropertiesBinderValidator<T> omitProperty(String key) {
		propertiesToRemove.add(key);
		return this;
	}

	public T build() throws BindException {
		Properties properties = loadYamlProperties(fileName);
		handlePropertyRemovals(properties);
		handlePropertyOverrides(properties);
		PropertiesPropertySource propertySource = new PropertiesPropertySource(fileName + " with overrides",
				properties);
		BindHandler handler = new ValidationBindHandler(validator);
		Binder binder = new Binder(ConfigurationPropertySources.from(propertySource));
		return binder.bindOrCreate(prefix, Bindable.of(targetClass), handler);
	}

	private void handlePropertyOverrides(Properties properties) {
		propertyOverrides.forEach(properties::put);
	}

	private void handlePropertyRemovals(Properties properties) {
		propertiesToRemove.forEach(properties::remove);
	}

	private Properties loadYamlProperties(String fileName) {
		Resource resource = new ClassPathResource(fileName);
		YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
		factoryBean.setResources(resource);
		return factoryBean.getObject();
	}

}