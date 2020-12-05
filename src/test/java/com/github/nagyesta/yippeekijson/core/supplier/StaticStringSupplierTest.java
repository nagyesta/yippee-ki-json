package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@LaunchAbortArmed
class StaticStringSupplierTest {

    private static final String A = "a";
    private static final String ABC = "abc";
    private static final String A_LONGER_MESSAGE = "a longer message";

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {A, ABC, A_LONGER_MESSAGE})
    void testGetShouldReturnTheStaticString(final String input) {
        //given
        final StaticStringSupplier underTest = new StaticStringSupplier(input);

        //when
        final String actual = underTest.get();

        //then
        Assertions.assertEquals(input, actual);
    }

    @Test
    void testConstructorShouldNotAllowNulls() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StaticStringSupplier(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {A, ABC, A_LONGER_MESSAGE})
    void testToStringShouldContainClassNameAndKey(final String key) {
        //given
        final StaticStringSupplier underTest = new StaticStringSupplier(key);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(StaticStringSupplier.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(key));
    }
}
