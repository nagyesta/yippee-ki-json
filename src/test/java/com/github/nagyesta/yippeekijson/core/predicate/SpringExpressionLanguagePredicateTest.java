package com.github.nagyesta.yippeekijson.core.predicate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpringExpressionLanguagePredicateTest {
    private static final String IS_FORTY_TWO = "(#root instanceof T(Integer)) && (#root < 43) && (#root > 41)";
    private static final String MAP_ENTRY_WITH_KEY_HAS_VALUE_FORTY_TWO = "(#root instanceof T(java.util.Map)) && #root['key'] == 42";
    private static final int INT_42 = 42;
    private static final String KEY = "key";

    private static Stream<Arguments> positiveMatchSupplier() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(IS_FORTY_TWO, INT_42))
                .add(Arguments.of(MAP_ENTRY_WITH_KEY_HAS_VALUE_FORTY_TWO, Map.of(KEY, INT_42)))
                .build();
    }

    private static Stream<Arguments> negativeMatchSupplier() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(IS_FORTY_TWO, null))
                .add(Arguments.of(IS_FORTY_TWO, false))
                .add(Arguments.of(IS_FORTY_TWO, Map.of(KEY, INT_42)))
                .build();
    }

    @ParameterizedTest
    @MethodSource("positiveMatchSupplier")
    void testTestShouldMatchForPositiveCases(final String expression, final Object value) {
        //given
        final SpringExpressionLanguagePredicate underTest = new SpringExpressionLanguagePredicate(expression);

        //when
        final boolean actual = underTest.test(value);

        //then
        Assertions.assertTrue(actual);
    }

    @ParameterizedTest
    @MethodSource("negativeMatchSupplier")
    void testTestShouldNotMatchForNegativeCases(final String expression, final Object value) {
        //given
        final SpringExpressionLanguagePredicate underTest = new SpringExpressionLanguagePredicate(expression);

        //when
        final boolean actual = underTest.test(value);

        //then
        Assertions.assertFalse(actual);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testTestShouldFailForInvalidInput() {
        //given
        final SpringExpressionLanguagePredicate underTest = new SpringExpressionLanguagePredicate(MAP_ENTRY_WITH_KEY_HAS_VALUE_FORTY_TWO);
        final Map<String, Integer> map = mock(Map.class);
        when(map.get(eq(KEY))).thenThrow(new IllegalStateException());

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.test(map));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"#{fn:contains(\"hello\", \"world\"}", "#{"})
    void testConstructorShouldFailForInvalidInput(final String expression) {
        //given
        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new SpringExpressionLanguagePredicate(expression));
    }

    @ParameterizedTest
    @ValueSource(strings = {IS_FORTY_TWO})
    void testToStringShouldContainClassNameAndExpression(final String expression) {
        //given
        final SpringExpressionLanguagePredicate underTest = new SpringExpressionLanguagePredicate(expression);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(SpringExpressionLanguagePredicate.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(expression));
    }
}
