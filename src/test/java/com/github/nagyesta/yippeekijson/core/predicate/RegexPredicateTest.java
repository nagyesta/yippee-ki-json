package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

@LaunchAbortArmed
class RegexPredicateTest {

    private static final String BOOLEAN = "^(true|True|TRUE|yes|YES|Yes|false|False|FALSE|no|NO|No)$";
    private static final String TRUE = "TRUE";
    private static final String FALSE = "FALSE";
    private static final String CAMEL_FALSE = "False";
    private static final String LOWER_TRUE = "true";
    private static final String LOWER_CASE = "[a-z]+";

    private static Stream<Arguments> positiveMatchSupplier() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(BOOLEAN, TRUE))
                .add(Arguments.of(BOOLEAN, LOWER_TRUE))
                .add(Arguments.of(BOOLEAN, FALSE))
                .add(Arguments.of(BOOLEAN, CAMEL_FALSE))
                .add(Arguments.of(TRUE, TRUE))
                .add(Arguments.of(LOWER_CASE, LOWER_TRUE))
                .build();
    }

    private static Stream<Arguments> negativeMatchSupplier() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(BOOLEAN, null))
                .add(Arguments.of(TRUE, null))
                .add(Arguments.of(TRUE, FALSE))
                .add(Arguments.of(BOOLEAN, BOOLEAN))
                .add(Arguments.of(TRUE, BOOLEAN))
                .add(Arguments.of(LOWER_CASE, LOWER_CASE))
                .build();
    }

    @ParameterizedTest
    @MethodSource("positiveMatchSupplier")
    void testTestShouldMatchForPositiveCases(final String key, final String value) {
        //given
        final RegexPredicate underTest = new RegexPredicate(key);

        //when
        final boolean actual = underTest.test(value);

        //then
        Assertions.assertTrue(actual);
    }

    @ParameterizedTest
    @MethodSource("negativeMatchSupplier")
    void testTestShouldNotMatchForNegativeCases(final String key, final String value) {
        //given
        final RegexPredicate underTest = new RegexPredicate(key);

        //when
        final boolean actual = underTest.test(value);

        //then
        Assertions.assertFalse(actual);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"+.)(", "{illegal}"})
    void testConstructorShouldFailForInvalidInput(final String pattern) {
        //given
        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new RegexPredicate(pattern));
    }

    @ParameterizedTest
    @ValueSource(strings = {BOOLEAN, TRUE, LOWER_CASE})
    void testToStringShouldContainClassNameAndPattern(final String pattern) {
        //given
        final RegexPredicate underTest = new RegexPredicate(pattern);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(RegexPredicate.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(pattern));
    }
}
