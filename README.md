# Spring Boot Reference

_Spring Boot reference app showing many common use cases and patterns_

## Running Locally

1. Run `SpringBootApp`
1. Starts @ http://localhost:8080

```bash
$ curl -s http://localhost:8080/api/headers | jq
{
  "headers": {
    "Accept": "application/json, application/*+json",
    "Host": "httpbin.org",
    "User-Agent": "Java/11.0.8",
    "X-Amzn-Trace-Id": "Root=1-5fee3b73-68471f0f53860c013ba47a46"
  }
}
```

## Immutable Validated Configuration

_Immutable config using Spring ConfigurationProperties and validation_

See `AppConfig`, `ConfigValidationTest`, and `application.yml` (main and test)

### Error With Validation

```text
***************************
APPLICATION FAILED TO START
***************************

Description:

Binding to target org.springframework.boot.context.properties.bind.BindException: Failed to bind properties under 'app' to com.davidagood.springimmutablevalidatedconfig.AppConfig failed:

    Property: app.userApi
    Value: null
    Reason: must not be null
```

```
    Property: app.user-api.url
    Value: htp://example.com
    Origin: class path resource [application.yml]:5:10
    Reason: must be a valid URL
```


### Error Without Validation

```text
java.lang.IllegalStateException: Failed to execute CommandLineRunner
	at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:787) ~[spring-boot-2.2.4.RELEASE.jar:2.2.4.RELEASE]
	at org.springframework.boot.SpringApplication.callRunners(SpringApplication.java:768) ~[spring-boot-2.2.4.RELEASE.jar:2.2.4.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:322) ~[spring-boot-2.2.4.RELEASE.jar:2.2.4.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1226) ~[spring-boot-2.2.4.RELEASE.jar:2.2.4.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1215) ~[spring-boot-2.2.4.RELEASE.jar:2.2.4.RELEASE]
	at com.davidagood.springimmutablevalidatedconfig.SpringImmutableValidatedConfigApplication.main(SpringImmutableValidatedConfigApplication.java:12) ~[main/:na]
Caused by: java.lang.NullPointerException: null
	at com.davidagood.springimmutablevalidatedconfig.ConfigLoggingRunner.run(ConfigLoggingRunner.java:19) ~[main/:na]
	at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:784) ~[spring-boot-2.2.4.RELEASE.jar:2.2.4.RELEASE]
	... 5 common frames omitted
```

# Spring Boot Configuration Processor

Doesn't work for some reason with immutable value classes...

## TODO

- Swagger
- Actuator
- JDBC/JPA
