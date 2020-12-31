package com.davidagood.springbootreference;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

@Mapper
public interface HeadersMapper {

	HeadersMapper INSTANCE = Mappers.getMapper(HeadersMapper.class);

	// See README: MapStruct Issue - Variable already defined
	@Mapping(source = "createdAt", target = "createdAt")
	HeadersDto map(Headers headers, Instant createdAt);

}
