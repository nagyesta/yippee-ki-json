package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EvalOnPredicateTest {

    static final Predicate<Object> IS_NULL = Objects::isNull;
    static final Predicate<Object> INTEGER = Integer.class::isInstance;
    static final Predicate<Object> STRING = String.class::isInstance;
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final Integer INT_42 = 42;
    private static final Map<String, RawConfigParam> EMPTY_MAP = Map.of();
    private static final Map<String, List<String>> WRONG_MAP = Map.of(VALUE, List.of(VALUE, KEY));
    private static final Map<String, String> FILLED_MAP = Map.of(KEY, VALUE);

    private static Stream<Arguments> validProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(true, EMPTY_MAP, KEY, IS_NULL))
                .add(Arguments.of(false, null, KEY, INTEGER))
                .add(Arguments.of(false, EMPTY_MAP, KEY, STRING))
                .add(Arguments.of(false, VALUE, KEY, STRING))
                .add(Arguments.of(false, INT_42, KEY, STRING))
                .add(Arguments.of(false, WRONG_MAP, KEY, STRING))
                .add(Arguments.of(false, WRONG_MAP, VALUE + "." + KEY, STRING))
                .add(Arguments.of(true, FILLED_MAP, KEY, STRING))
                .add(Arguments.of(false, EMPTY_MAP, KEY + "." + KEY, STRING))
                .build();
    }

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null, null))
                .add(Arguments.of(KEY, null, null))
                .add(Arguments.of(null, EMPTY_MAP, null))
                .add(Arguments.of(null, null, mock(FunctionRegistry.class)))
                .add(Arguments.of(KEY, EMPTY_MAP, null))
                .add(Arguments.of(KEY, null, mock(FunctionRegistry.class)))
                .add(Arguments.of(null, EMPTY_MAP, mock(FunctionRegistry.class)))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validProvider")
    void testTestShouldEvaluateChildren(final boolean expected, final Object value, final String key, final Predicate<Object> predicate) {
        //given
        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        when(functionRegistry.lookupPredicate(anyMap())).thenReturn(predicate);

        EvalOnPredicate underTest = new EvalOnPredicate(key, EMPTY_MAP, functionRegistry);

        //when
        final boolean actual = underTest.test(value);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testToStringShouldContainClassName() {
        //given
        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        when(functionRegistry.lookupPredicate(anyMap())).thenReturn(IS_NULL);

        EvalOnPredicate underTest = new EvalOnPredicate(KEY, EMPTY_MAP, functionRegistry);

        //when
        final String toString = underTest.toString();

        //then
        Assertions.assertTrue(toString.contains(EvalOnPredicate.class.getSimpleName()));
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldNotAllowNulls(final String childPath,
                                            final Map<String, RawConfigParam> predicate,
                                            final FunctionRegistry functionRegistry) {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new EvalOnPredicate(childPath, predicate, functionRegistry));
    }
}
