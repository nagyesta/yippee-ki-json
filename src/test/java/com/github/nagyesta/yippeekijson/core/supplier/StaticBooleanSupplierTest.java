package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@LaunchAbortArmed
class StaticBooleanSupplierTest {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testGetShouldReturnTheStaticString(final Boolean input) {
        //given
        final StaticBooleanSupplier underTest = new StaticBooleanSupplier(input);

        //when
        final Boolean actual = underTest.get();

        //then
        Assertions.assertEquals(input, actual);
    }

    @Test
    void testConstructorShouldNotAllowNulls() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StaticBooleanSupplier(null));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testToStringShouldContainClassNameAndKey(final Boolean input) {
        //given
        final StaticBooleanSupplier underTest = new StaticBooleanSupplier(input);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(StaticBooleanSupplier.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(Boolean.toString(input)));
    }
}
