package com.davidagood.springbootreference;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

@Mapper
public interface HeadersMapper {

	HeadersMapper INSTANCE = Mappers.getMapper(HeadersMapper.class);

	HeadersDto map(Headers headers, Instant createdAt);

}
