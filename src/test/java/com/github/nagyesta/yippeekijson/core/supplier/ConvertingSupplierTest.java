package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.nagyesta.yippeekijson.test.helper.TestResourceProvider.JSON_VALIDATION_TEST_SCHEMA;
import static com.github.nagyesta.yippeekijson.test.helper.TestResourceProvider.resource;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConvertingSupplierTest {

    private static final String EMPTY_JSON = "{}";

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null, null))
                .add(Arguments.of(Map.of(), null, null))
                .add(Arguments.of(null, Map.of(), null))
                .add(Arguments.of(null, null, mock(FunctionRegistry.class)))
                .add(Arguments.of(Map.of(), Map.of(), null))
                .add(Arguments.of(Map.of(), null, mock(FunctionRegistry.class)))
                .add(Arguments.of(null, Map.of(), mock(FunctionRegistry.class)))
                .build();
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldNotAllowNulls(final Map<String, RawConfigParam> supplier,
                                            final Map<String, RawConfigParam> function,
                                            final FunctionRegistry functionRegistry) {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ConvertingSupplier(supplier, function, functionRegistry));
    }

    @Test
    void testGetShouldThrowExceptionWhenSourceSupplierFails() {
        //given
        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        when(functionRegistry.lookupSupplier(anyMap())).thenReturn(() -> {
            throw new IllegalArgumentException();
        });
        when(functionRegistry.lookupFunction(anyMap())).thenReturn(Function.identity());
        final ConvertingSupplier underTest = new ConvertingSupplier(Map.of(), Map.of(), functionRegistry);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, underTest::get);
    }

    @Test
    void testGetShouldReturnConvertedValueOfSourceWhenSourceSupplierReturnsValidInput() throws IOException {
        //given
        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        when(functionRegistry.<String>lookupSupplier(anyMap())).thenReturn(() -> JSON_VALIDATION_TEST_SCHEMA);
        when(functionRegistry.<String, String>lookupFunction(anyMap())).thenReturn(value -> resource().asString(value));
        final ConvertingSupplier underTest = new ConvertingSupplier(Map.of(), Map.of(), functionRegistry);

        //when
        final Object actual = underTest.get();

        //then
        String expected = resource().asString(JSON_VALIDATION_TEST_SCHEMA);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testToStringShouldContainClassNameSupplierAndFunction() {
        //given
        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        when(functionRegistry.lookupSupplier(anyMap())).thenReturn(() -> EMPTY_JSON);
        when(functionRegistry.lookupFunction(anyMap())).thenReturn(Function.identity());
        final ConvertingSupplier underTest = new ConvertingSupplier(Map.of(), Map.of(), functionRegistry);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(ConvertingSupplier.class.getSimpleName()));
        Assertions.assertTrue(actual.contains("stringSupplier"));
        Assertions.assertTrue(actual.contains("converterFunction"));
    }
}
