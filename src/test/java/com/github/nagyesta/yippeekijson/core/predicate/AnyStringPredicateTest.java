package com.github.nagyesta.yippeekijson.core.predicate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class AnyStringPredicateTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"a", "abc", "a longer message"})
    void testTestShouldMatchAnyString(final String input) {
        //given
        final AnyStringPredicate underTest = new AnyStringPredicate();

        //when
        final boolean actual = underTest.test(input);

        //then
        Assertions.assertTrue(actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void testTestShouldNotMatchNonStrings(final Object input) {
        //given
        final AnyStringPredicate underTest = new AnyStringPredicate();

        //when
        final boolean actual = underTest.test(input);

        //then
        Assertions.assertFalse(actual);
    }

    @Test
    void testToStringShouldContainClassName() {
        //given
        final AnyStringPredicate underTest = new AnyStringPredicate();

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(AnyStringPredicate.class.getSimpleName()));
    }
}
