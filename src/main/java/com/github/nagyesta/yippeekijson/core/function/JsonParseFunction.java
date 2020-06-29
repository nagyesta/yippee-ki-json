package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.exception.AbortTransformationException;
import com.jayway.jsonpath.JsonPath;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.StringJoiner;
import java.util.function.Function;

/**
 * {@link Function} for parsing a JSON {@link String} into a {@link Object}.
 */
@Slf4j
public final class JsonParseFunction implements Function<String, Object> {

    static final String NAME = "jsonParse";

    private final JsonMapper jsonMapper;

    @NamedFunction(NAME)
    public JsonParseFunction(@NonNull final JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public Object apply(final String s) {
        try {
            return JsonPath.parse(s, jsonMapper.parserConfiguration()).json();
        } catch (final Exception e) {
            log.error("Failed to parse input: " + e.getMessage(), e);
            throw new AbortTransformationException("Failed to parse input: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JsonParseFunction.class.getSimpleName() + "[", "]")
                .toString();
    }
}
