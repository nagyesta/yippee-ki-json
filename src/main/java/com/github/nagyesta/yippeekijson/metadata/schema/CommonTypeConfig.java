package com.github.nagyesta.yippeekijson.metadata.schema;

import com.github.nagyesta.yippeekijson.core.http.HttpMethod;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.CommonMapTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.CommonStringTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonCommonTypeDefinitionRegistry;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.typehelper.StringStringMap;
import com.jayway.jsonpath.JsonPath;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * Defines common types and their mappings.
 */
public abstract class CommonTypeConfig {

    private static final String DOT_OR_DOT_DOT = "(\\.|\\.\\.)";
    private static final String INDEX = "[0-9]+";
    private static final String ASTERISK = "\\*";
    private static final String JSON_KEY_NAME = "[$_a-zA-Z]+[$a-zA-Z0-9\\-_]*";
    private static final String OR = "|";
    private static final String JSON_KEY_NAME_OR_ASTERISK = "(" + JSON_KEY_NAME + OR + ASTERISK + ")";
    private static final String JSON_KEY_NAME_EXACT_MATCH = "^" + JSON_KEY_NAME + "$";
    private static final String JSON_KEY_WITH_DOT_NOTATION = "(" + DOT_OR_DOT_DOT + JSON_KEY_NAME_OR_ASTERISK + ")";
    private static final String JSON_KEY_WITH_ARRAY_NOTATION = "\\[" + ASTERISK + "]";
    private static final String JSON_INDEX_OR_INDICES_WITH_ARRAY_NOTATION = "\\[" + INDEX
            + "((, " + INDEX + ")*" + OR + ":" + INDEX + ")]";
    private static final String JSON_KEY_SEQUENCE_WITH_ARRAY_NOTATION = "\\['" + JSON_KEY_NAME
            + "'(, '" + JSON_KEY_NAME + "')*]";
    private static final String JSON_PREDICATE_PATTERN = "\\[\\?\\(.+\\)])*";
    private static final String JSON_PATH_PATTERN = "[$@](" + JSON_KEY_WITH_DOT_NOTATION
            + OR + JSON_KEY_WITH_ARRAY_NOTATION
            + OR + JSON_INDEX_OR_INDICES_WITH_ARRAY_NOTATION
            + OR + JSON_KEY_SEQUENCE_WITH_ARRAY_NOTATION
            + OR + JSON_PREDICATE_PATTERN;
    private static final String JSON_PATH_EXACT_MATCH = "^" + JSON_PATH_PATTERN + "$";

    /**
     * Evaluates all of the annotated methods of this class and registers them to the registry.
     *
     * @param registry the registry holding the common types
     */
    public static void registerTo(@NotNull final JsonCommonTypeDefinitionRegistry registry) {
        Arrays.stream(CommonTypeConfig.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(CommonStringTypeDefinition.class))
                .forEach(method -> {
                    registry.registerType(method.getReturnType(), method.getDeclaredAnnotation(CommonStringTypeDefinition.class));
                });
        Arrays.stream(CommonTypeConfig.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(CommonMapTypeDefinition.class))
                .forEach(method -> {
                    registry.registerType(method.getReturnType(), method.getDeclaredAnnotation(CommonMapTypeDefinition.class));
                });
    }

    @CommonStringTypeDefinition(
            typeName = "jsonPath",
            regex = JSON_PATH_EXACT_MATCH,
            docs = "A JSON Path selecting one or more nodes of the parsed JSON document.",
            wikiLink = @WikiLink(uri = "https://github.com/json-path/JsonPath")
    )
    public abstract JsonPath pathDefinition();

    @CommonStringTypeDefinition(
            typeName = "name",
            docs = "A single JSON key name",
            regex = JSON_KEY_NAME_EXACT_MATCH
    )
    public abstract JsonNameType jsonKeyName();

    @CommonStringTypeDefinition(
            typeName = "chronoUnit",
            enumType = ChronoUnit.class,
            docs = "The chrono unit we want to use."
    )
    public abstract ChronoUnit chronoUnit();

    @CommonStringTypeDefinition(
            typeName = "charSet",
            values = {"UTF-8",
                    "UTF-16",
                    "UTF-16LE",
                    "UTF-16BE",
                    "ISO-8859-1",
                    "US-ASCII"
            },
            docs = "Standard character sets."
    )
    public abstract Charset charset();

    @CommonStringTypeDefinition(
            typeName = "httpMethod",
            enumType = HttpMethod.class,
            docs = "Supported HTTP methods."
    )
    public abstract HttpMethod httpMethod();

    @CommonMapTypeDefinition(
            typeName = "httpHeaders",
            docs = "HTTP header map."
    )
    public abstract StringStringMap httpHeaders();

    private static final class JsonNameType {
    }
}
