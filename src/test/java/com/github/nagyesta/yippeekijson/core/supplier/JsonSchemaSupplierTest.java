package com.github.nagyesta.yippeekijson.core.supplier;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.JsonMapperImpl;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import com.github.nagyesta.yippeekijson.core.exception.AbortTransformationException;
import com.networknt.schema.JsonSchema;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JsonSchemaSupplierTest {

    private static final String EMPTY_JSON = "{}";
    private static final String TEST_SCHEMA_JSON = "/validation/test-schema.json";
    private static final String PROPERTIES = "properties";
    private static final String OBJECT = "object";
    private static final String ARRAY = "array";
    private static final String OBJECT_ARRAY = "object.array";
    private static final String OBJECT_DOTS_ARRAY = "object...array";

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null, null))
                .add(Arguments.of(Map.of(), null, null))
                .add(Arguments.of(null, mock(JsonMapper.class), null))
                .add(Arguments.of(null, null, mock(FunctionRegistry.class)))
                .add(Arguments.of(Map.of(), mock(JsonMapper.class), null))
                .add(Arguments.of(Map.of(), null, mock(FunctionRegistry.class)))
                .add(Arguments.of(null, mock(JsonMapper.class), mock(FunctionRegistry.class)))
                .build();
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldNotAllowNulls(final Map<String, RawConfigParam> map,
                                            final JsonMapper jsonMapper,
                                            final FunctionRegistry functionRegistry) {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new JsonSchemaSupplier(map, jsonMapper, functionRegistry));
    }

    @Test
    void testGetShouldThrowAbortExceptionWhenSourceSupplierFails() {
        //given
        JsonMapper jsonMapper = new JsonMapperImpl();
        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        when(functionRegistry.lookupSupplier(anyMap())).thenReturn(() -> {
            throw new IllegalArgumentException();
        });
        final JsonSchemaSupplier underTest = new JsonSchemaSupplier(Map.of(), jsonMapper, functionRegistry);

        //when + then exception
        Assertions.assertThrows(AbortTransformationException.class, underTest::get);
    }

    @Test
    void testGetShouldReturnSchemaWhenSourceSupplierReturnsValidInput() throws IOException {
        //given
        String schemaSource = IOUtils.resourceToString(TEST_SCHEMA_JSON, StandardCharsets.UTF_8);
        JsonMapper jsonMapper = new JsonMapperImpl();
        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        when(functionRegistry.lookupSupplier(anyMap())).thenReturn(() -> schemaSource);
        final JsonSchemaSupplier underTest = new JsonSchemaSupplier(Map.of(), jsonMapper, functionRegistry);

        //when
        final JsonSchema actual = underTest.get();

        //then
        Assertions.assertNotNull(actual);
        Assertions.assertTrue(actual.getSchemaNode().hasNonNull(PROPERTIES));
        final JsonNode properties = actual.getSchemaNode().get(PROPERTIES);
        Assertions.assertTrue(properties.hasNonNull(OBJECT));
        Assertions.assertTrue(properties.hasNonNull(ARRAY));
        Assertions.assertTrue(properties.hasNonNull(OBJECT_ARRAY));
        Assertions.assertTrue(properties.hasNonNull(OBJECT_DOTS_ARRAY));
    }

    @Test
    void testToStringShouldContainClassName() {
        //given
        JsonMapper jsonMapper = new JsonMapperImpl();
        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        when(functionRegistry.lookupSupplier(anyMap())).thenReturn(() -> EMPTY_JSON);
        final JsonSchemaSupplier underTest = new JsonSchemaSupplier(Map.of(), jsonMapper, functionRegistry);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(JsonSchemaSupplier.class.getSimpleName()));
    }
}
