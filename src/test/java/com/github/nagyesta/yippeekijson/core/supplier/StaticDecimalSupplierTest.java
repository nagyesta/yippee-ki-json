package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

@LaunchAbortArmed
class StaticDecimalSupplierTest {

    private static final String DOUBLE_1_0 = "1.0";
    private static final String DOUBLE_PI = "3.1415";
    private static final String INT_42 = "42";

    @ParameterizedTest
    @ValueSource(strings = {DOUBLE_1_0, DOUBLE_PI, INT_42})
    void testGetShouldReturnTheStaticString(final String input) {
        //given
        final BigDecimal expected = new BigDecimal(input);
        final StaticDecimalSupplier underTest = new StaticDecimalSupplier(expected);

        //when
        final BigDecimal actual = underTest.get();

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testConstructorShouldNotAllowNulls() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StaticDecimalSupplier(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {DOUBLE_1_0, DOUBLE_PI, INT_42})
    void testToStringShouldContainClassNameAndKey(final String input) {
        //given
        final BigDecimal expected = new BigDecimal(input);
        final StaticDecimalSupplier underTest = new StaticDecimalSupplier(expected);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(StaticDecimalSupplier.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(input));
    }
}
