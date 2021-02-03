package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;

@LaunchAbortArmed
class StaticIntegerSupplierTest {

    private static final int INT_1 = 1;
    private static final int INT_2 = 2;
    private static final int INT_42 = 42;

    @ParameterizedTest
    @ValueSource(ints = {INT_1, INT_2, INT_42})
    void testGetShouldReturnTheStaticString(final int input) {
        //given
        final BigInteger expected = BigInteger.valueOf(input);
        final StaticIntegerSupplier underTest = new StaticIntegerSupplier(expected);

        //when
        final BigInteger actual = underTest.get();

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testConstructorShouldNotAllowNulls() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StaticIntegerSupplier(null));
    }

    @ParameterizedTest
    @ValueSource(ints = {INT_1, INT_2, INT_42})
    void testToStringShouldContainClassNameAndKey(final int input) {
        //given
        final BigInteger expected = BigInteger.valueOf(input);
        final StaticIntegerSupplier underTest = new StaticIntegerSupplier(expected);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(StaticIntegerSupplier.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(String.valueOf(input)));
    }
}
