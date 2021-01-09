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

## Transparently Make OAuth 2 Client Credentials Authorization Request using WebClient

Related blog posts:
- [How to Automatically Refresh OAuth2 Client Credentials in Spring](https://davidagood.com/oauth-client-credentials-auto-refresh-spring/)
- [How To Completely Disable HTTP Security in Spring Security](https://davidagood.com/spring-security-disable-http-security/)

This is demonstrated through an integration test, `AuthorizedWebClientIT` rather than
live calls to authorization and resource servers. 

The Spring configuration which enables all of this is 
in `AuthorizedWebClientConfig` with overrides for integration testing 
in `AuthorizedWebClientIT.TestConfig`.

### How This Is All Works Under The Hood

Almost all the classes mentioned below are in the package `org.springframework.security.oauth2.client`
or `org.springframework.security.oauth2.client.web`
from `org.springframework.security:spring-security-oauth2-client`...

1. `ServletOAuth2AuthorizedClientExchangeFilterFunction.filter`
   1. Takes the resource server request
   1. Uses existing authorization or, if necessary, handles making a new authorization request (see next step)
   1. Adds bearer token to resource server request
1. `ServletOAuth2AuthorizedClientExchangeFilterFunction.authorizeClient`
1. Calls `OAuth2AuthorizedClientManager.authorize`

Using the `AuthorizedClientServiceOAuth2AuthorizedClientManager` as the concrete `OAuth2AuthorizedClientManager`:

1. `AuthorizedClientServiceOAuth2AuthorizedClientManager.authorize`
   1. Calls its member `OAuth2AuthorizedClientService.loadAuthorizedClient`
1. `InMemoryOAuth2AuthorizedClientService.loadAuthorizedClient`
   1. Stores authorized clients in a `ConcurrentHashMap`
   1. The only parameters you need to retrieve an authorized client are:
      1. clientRegistrationId - whatever arbitrary registration id you've given, 
         e.g. in `spring.security.oauth2.client.registration.<client-registration-id-here>`
      1. principalName - "anonymousUser" in this case, 
         see `ServletOAuth2AuthorizedClientExchangeFilterFunction.ANONYMOUS_AUTHENTICATION`

Using the `DefaultOAuth2AuthorizedClientManager` as the concrete `OAuth2AuthorizedClientManager`:

1. `DefaultOAuth2AuthorizedClientManager.authorize`
   1. Call its member `OAuth2AuthorizedClientRepository.loadAuthorizedClient` 
1. `AuthenticatedPrincipalOAuth2AuthorizedClientRepository.loadAuthorizedClient`
   1. If the authenticated user is anonymous, defers to:
1. `HttpSessionOAuth2AuthorizedClientRepository.loadAuthorizedClient`
   1. This looks for the authorized client in the `HttpServletRequest`'s `HttpSession`
1. `DelegatingOAuth2AuthorizedClientProvider.authorize`
1. `ClientCredentialsOAuth2AuthorizedClientProvider.authorize`
   1. This is where it checks if an authorization request needs to be made 
      (if the authorized client is `null` or the token has expired)
   1. Returns `null` to signal that a new authorization request did not need to be made
      because an unexpired authorized client already existed in which case 
      the AuthorizedClientManager will just use the existing auth

### Other Info

What autowires a `ClientRegistrationRepository` (concrete class: `InMemoryClientRegistrationRepository`) if 
one is not already registered?

This: `org.springframework.boot.autoconfigure.security.oauth2.client.servletOAuth2ClientRegistrationRepositoryConfiguration`

It looks up registrations from application properties: `spring.security.oauth2.client.registration`

## MapStruct Lombok Issues
Lombok 1.18.16 was a breaking change for MapStruct. 
The [release notes](https://github.com/rzwitserloot/lombok/releases/tag/v1.18.16) say 
to add a dependency, `org.projectlombok:lombok-mapstruct-binding:0.2.0`, but that 
doesn't help. Downgraded to Lombok 1.18.14 for now.

## MapStruct Issue - Variable already defined

https://github.com/mapstruct/mapstruct/issues/2251

```java
public class HeadersMapperImpl implements HeadersMapper {

    @Override
    public HeadersDto map(Headers headers, Instant createdAt) {
        // ...
        Instant createdAt1 = null;
        if ( createdAt != null ) {
            createdAt1 = createdAt;
        }

        // Error here, variable with this name already defined!
        Instant createdAt1 = null;

        HeadersDto headersDto = new HeadersDto( headers1, createdAt1 );

        return headersDto;
    }
}
```

```text
.../build/generated/sources/annotationProcessor/java/main/com/davidagood/springbootreference/HeadersMapperImpl.java:53: 
error: variable createdAt1 is already defined in method map(Headers,Instant)
Instant createdAt1 = null;
```

## Spring Boot Configuration Processor

Doesn't work for some reason with immutable value classes...

## TODO

- Swagger
- Actuator
- JDBC/JPA
