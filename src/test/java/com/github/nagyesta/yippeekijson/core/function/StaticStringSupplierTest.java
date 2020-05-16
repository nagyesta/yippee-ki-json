package com.github.nagyesta.yippeekijson.core.function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StaticStringSupplierTest {

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"a", "abc", "a longer message"})
    void testGetShouldReturnTheStaticString(String input) {
        //given
        StaticStringSupplier underTest = new StaticStringSupplier(input);

        //when
        String actual = underTest.get();

        //then
        Assertions.assertEquals(input, actual);
    }

    @Test
    void testConstructorShouldNotAllowNulls() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StaticStringSupplier(null));
    }
}