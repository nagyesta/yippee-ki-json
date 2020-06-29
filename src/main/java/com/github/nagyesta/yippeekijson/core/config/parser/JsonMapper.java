package com.github.nagyesta.yippeekijson.core.config.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.TypeRef;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Main interface capturing mapping related behavior.
 */
public interface JsonMapper {

    /**
     * Returns the parser configuration we use in the context.
     *
     * @return the parser config
     */
    Configuration parserConfiguration();

    /**
     * Converts and input object to the desired format if possible.
     *
     * @param input   The input object
     * @param typeRef The type we would like to convert to
     * @param <T>     The type identified by the typeRef
     * @return the converted object
     */
    <T> T mapTo(@NonNull Object input, @NonNull TypeRef<T> typeRef);

    /**
     * Returns an ObjectMapper with the same configuration we use for parsing/mapping.
     *
     * @return objectMapper
     */
    @NotNull
    ObjectMapper objectMapper();

    /**
     * {@link TypeRef} implementation for {@link String} to {@link Object} {@link Map} type.
     */
    class MapTypeRef extends TypeRef<Map<String, Object>> {
        public static final MapTypeRef INSTANCE = new MapTypeRef();
    }
}
