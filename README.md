# Spring Boot Reference

Spring Boot reference app showing many common use cases and patterns


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

