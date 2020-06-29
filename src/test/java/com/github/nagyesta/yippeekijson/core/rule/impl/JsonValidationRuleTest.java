package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.JsonMapperImpl;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.exception.AbortTransformationException;
import com.github.nagyesta.yippeekijson.core.exception.StopRuleProcessingException;
import com.github.nagyesta.yippeekijson.core.rule.strategy.TransformationControlStrategy;
import com.github.nagyesta.yippeekijson.core.rule.strategy.ViolationStrategy;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.mapper.MappingException;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.nagyesta.yippeekijson.core.rule.impl.JsonValidationRule.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class JsonValidationRuleTest {

    private static final String ROOT_NOT_EXISTING_CHILD = "$.something";
    private static final String TEST_SCHEMA_JSON = "/validation/test-schema.json";
    private static final String INPUT_JSON = "/validation/validation-input.json";
    private static final String OUTPUT_JSON = "/validation/validation-output.json";

    private static Stream<Arguments> strategyProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(TransformationControlStrategy.SKIP_REST, StopRuleProcessingException.class))
                .add(Arguments.of(TransformationControlStrategy.ABORT, AbortTransformationException.class))
                .add(Arguments.of(TransformationControlStrategy.CONTINUE, null))
                .build();
    }

    @ParameterizedTest
    @MethodSource("strategyProvider")
    void testAcceptShouldHandleValidationIssuesAndIgnorePath(final TransformationControlStrategy strategy,
                                                             final Class<Exception> expectedException) throws JsonProcessingException {
        //given
        final JsonMapperImpl jsonMapper = new JsonMapperImpl();
        final ObjectMapper objectMapper = jsonMapper.objectMapper();
        JsonSchemaFactory factory = JsonSchemaFactory
                .builder(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7))
                .objectMapper(objectMapper)
                .build();
        final JsonSchema jsonSchema = factory.getSchema(getResource(TEST_SCHEMA_JSON));

        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        RawJsonRule jsonRule = RawJsonRule.builder()
                .name(RULE_NAME)
                .putParams(Map.of(
                        PARAM_SCHEMA, Map.of(),
                        PARAM_ON_FAILURE, Map.of(
                                PARAM_ON_FAILURE_TRANSFORMATION, strategy.name(),
                                PARAM_ON_FAILURE_VIOLATION, ViolationStrategy.COMMENT_JSON.name())))
                .order(0)
                .path(ROOT_NOT_EXISTING_CHILD)
                .build();
        when(functionRegistry.lookupSupplier(anyMap())).thenReturn(() -> jsonSchema);

        JsonValidationRule underTest = new JsonValidationRule(functionRegistry, jsonRule);
        DocumentContext documentContext = JsonPath.parse(getResource(INPUT_JSON), jsonMapper.parserConfiguration());

        //when
        if (expectedException == null) {
            Assertions.assertDoesNotThrow(() -> underTest.accept(documentContext));
        } else {
            Assertions.assertThrows(expectedException, () -> underTest.accept(documentContext));
        }

        //then
        final String actualJson = documentContext.jsonString();
        final String expectedJson = getResource(OUTPUT_JSON);

        final JsonNode actual = objectMapper.readTree(actualJson);
        final JsonNode expected = objectMapper.readTree(expectedJson);

        Assertions.assertEquals(expected, actual);
    }

    private String getResource(final String resourcePath) {
        try {
            return IOUtils.resourceToString(resourcePath, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Test
    void testAcceptShouldHandleMappingExceptions() {
        //given
        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        RawJsonRule jsonRule = RawJsonRule.builder()
                .name(RULE_NAME)
                .putParams(Map.of(
                        PARAM_SCHEMA, Map.of(),
                        PARAM_ON_FAILURE, Map.of(
                                PARAM_ON_FAILURE_TRANSFORMATION, TransformationControlStrategy.CONTINUE.name(),
                                PARAM_ON_FAILURE_VIOLATION, ViolationStrategy.COMMENT_JSON.name())))
                .order(0)
                .path(ROOT_NOT_EXISTING_CHILD)
                .build();
        when(functionRegistry.lookupSupplier(anyMap())).thenReturn(() -> null);

        JsonValidationRule underTest = new JsonValidationRule(functionRegistry, jsonRule);
        DocumentContext documentContext = mock(DocumentContext.class);
        doThrow(MappingException.class).when(documentContext).read(anyString(), eq(JsonNode.class));

        //when + then exception
        Assertions.assertThrows(IllegalStateException.class, () -> underTest.accept(documentContext));
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {ROOT_NOT_EXISTING_CHILD})
    void testConstructorShouldFailForInvalidEnumName(final String enumName) {
        //given
        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        RawJsonRule jsonRule = RawJsonRule.builder()
                .name(RULE_NAME)
                .putParams(Map.of(
                        PARAM_SCHEMA, Map.of(),
                        PARAM_ON_FAILURE, Map.of(
                                PARAM_ON_FAILURE_TRANSFORMATION, TransformationControlStrategy.CONTINUE.name(),
                                PARAM_ON_FAILURE_VIOLATION, enumName)))
                .order(0)
                .path(ROOT_NOT_EXISTING_CHILD)
                .build();
        when(functionRegistry.lookupSupplier(anyMap())).thenReturn(() -> null);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new JsonValidationRule(functionRegistry, jsonRule));
    }
}
