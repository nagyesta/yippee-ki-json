package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.exception.AbortTransformationException;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
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

    @SchemaDefinition(
            inputType = String.class,
            outputType = Object.class,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_FUNCTIONS, section = "JSON parse function"),
            sinceVersion = WikiConstants.VERSION_1_2_0,
            description = {
                    "This function parses the input JSON String and returns an object."
            },
            example = @Example(
                    in = "/examples/json/simple-accounts_in.json",
                    out = "/examples/json/add-string-json_out.json",
                    yml = "/examples/yml/add-string-json-parse.yml",
                    note = "In this example we have parsed the string and inserted an object instead.")
    )
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
