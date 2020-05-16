package com.github.nagyesta.yippeekijson.core.function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class AnyStringPredicateTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"a", "abc", "a longer message"})
    void testTestShouldMatchAnyString(String input) {
        //given
        AnyStringPredicate underTest = new AnyStringPredicate();

        //when
        boolean actual = underTest.test(input);

        //then
        Assertions.assertTrue(actual);
    }
}