package com.github.nagyesta.yippeekijson.core.config.parser.raw.params;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

class RawConfigValueTest {

    private static final String CONFIG_PATH = "config";
    private static final String VALUE = "value";

    @Test
    void testInitConvertedShouldNotMakeTransformations() {
        //given
        RawConfigValue underTest = new RawConfigValue(CONFIG_PATH, VALUE);

        //when
        final String actual = underTest.initConverted(VALUE);

        //then
        Assertions.assertEquals(VALUE, actual);
    }

    @Test
    void testAsStringShouldReturnTheConvertedString() {
        //given
        RawConfigValue underTest = new RawConfigValue(CONFIG_PATH, VALUE);

        //when
        final String actual = underTest.asString();

        //then
        Assertions.assertEquals(VALUE, actual);
    }

    @Test
    void testAsStringsShouldReturnTheConvertedCollection() {
        //given
        RawConfigValue underTest = new RawConfigValue(CONFIG_PATH, VALUE);

        //when
        final Collection<String> actual = underTest.asStrings();

        //then
        Assertions.assertEquals(List.of(VALUE), actual);
    }

    @Test
    void testIsRepeatedShouldReturnFalse() {
        //given
        RawConfigValue underTest = new RawConfigValue(CONFIG_PATH, VALUE);

        //when
        final boolean actual = underTest.isRepeated();

        //then
        Assertions.assertFalse(actual);
    }
}
