package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

@LaunchAbortArmed
class IsNullPredicateTest {

    private static final String KEY = "key";
    private static final String VALUE = "value";

    private static Stream<Arguments> negativeMatchSupplier() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(VALUE))
                .add(Arguments.of(Boolean.TRUE))
                .add(Arguments.of(Map.of(KEY, VALUE)))
                .add(Arguments.of(new Object()))
                .build();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testTestShouldMatchForPositiveCases() {
        //given
        final Object obj = null;
        final IsNullPredicate underTest = new IsNullPredicate();

        //when
        final boolean actual = underTest.test(obj);

        //then
        Assertions.assertTrue(actual);
    }

    @ParameterizedTest
    @MethodSource("negativeMatchSupplier")
    void testTestShouldNotMatchForNegativeCases(final Object value) {
        //given
        final IsNullPredicate underTest = new IsNullPredicate();

        //when
        final boolean actual = underTest.test(value);

        //then
        Assertions.assertFalse(actual);
    }

    @Test
    void testToStringShouldContainClassName() {
        //given
        final IsNullPredicate underTest = new IsNullPredicate();

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(IsNullPredicate.class.getSimpleName()));
    }
}
