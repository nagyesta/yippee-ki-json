package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.fasterxml.jackson.databind.*;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public class JsonMapperImpl implements JsonMapper {

    @Override
    public Configuration parserConfiguration() {
        return Configuration.builder()
                .jsonProvider(jsonProvider())
                .mappingProvider(mappingProvider())
                .build();
    }

    @Override
    public <T> T mapTo(@NonNull final Object input, @NonNull final TypeRef<T> typeRef) {
        return mappingProvider().map(input, typeRef, parserConfiguration());
    }

    @NotNull
    private JacksonMappingProvider mappingProvider() {
        return new JacksonMappingProvider(objectMapper());
    }

    @NotNull
    private JacksonJsonProvider jsonProvider() {
        return new JacksonJsonProvider(objectMapper());
    }

    @NotNull
    private ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        final SerializationConfig serializationConfig = objectMapper.getSerializationConfig()
                .withoutFeatures(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.setConfig(serializationConfig);
        final DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig()
                .with(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .with(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
        objectMapper.setConfig(deserializationConfig);
        return objectMapper;
    }
}
