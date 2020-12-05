package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@LaunchAbortArmed
class NoneMatchPredicateTest {

    static final Predicate<Object> IS_NULL = Objects::isNull;
    static final Predicate<Object> INTEGER = Integer.class::isInstance;
    static final Predicate<Object> STRING = String.class::isInstance;
    private static final String VALUE = "value";
    private static final Map<String, RawConfigParam> EMPTY_MAP = Map.of();
    @SuppressWarnings("unchecked")
    private static final Predicate<Object>[] IS_NULL_STRING = new Predicate[]{IS_NULL, STRING};
    @SuppressWarnings("unchecked")
    private static final Predicate<Object>[] IS_NULL_INTEGER = new Predicate[]{IS_NULL, INTEGER};

    private static Stream<Arguments> validProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(true, VALUE, IS_NULL_INTEGER))
                .add(Arguments.of(false, null, IS_NULL_INTEGER))
                .add(Arguments.of(false, null, IS_NULL_STRING))
                .add(Arguments.of(false, VALUE, IS_NULL_STRING))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validProvider")
    void testTestShouldEvaluateChildren(final boolean expected, final Object value, final Predicate<Object>[] predicates) {
        //given
        final List<Map<String, RawConfigParam>> dummyParams = Arrays.stream(predicates)
                .map(p -> EMPTY_MAP)
                .collect(Collectors.toList());

        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        final Predicate<Object> firstPredicate = predicates[0];
        final Predicate<Object>[] restOfPredicates = Arrays.copyOfRange(predicates, 1, predicates.length);
        when(functionRegistry.lookupPredicate(anyMap())).thenReturn(firstPredicate, restOfPredicates);

        NoneMatchPredicate underTest = new NoneMatchPredicate(dummyParams, functionRegistry);

        //when
        final boolean actual = underTest.test(value);

        //then
        Assertions.assertEquals(expected, actual);
    }
}
