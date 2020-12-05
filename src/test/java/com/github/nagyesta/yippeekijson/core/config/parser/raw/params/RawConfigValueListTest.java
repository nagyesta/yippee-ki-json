package com.github.nagyesta.yippeekijson.core.config.parser.raw.params;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

@LaunchAbortArmed
class RawConfigValueListTest {

    private static final String CONFIG_PATH = "config";
    private static final String VALUE = "value";

    @Test
    void testInitConvertedShouldNotMakeTransformations() {
        //given
        List<String> list = List.of(VALUE);
        RawConfigValueList underTest = new RawConfigValueList(CONFIG_PATH, list);

        //when
        final List<String> actual = underTest.initConverted(list);

        //then
        Assertions.assertEquals(list, actual);
    }

    @Test
    void testAsStringsShouldReturnTheConvertedCollection() {
        //given
        List<String> list = List.of(VALUE);
        RawConfigValueList underTest = new RawConfigValueList(CONFIG_PATH, list);

        //when
        final Collection<String> actual = underTest.asStrings();

        //then
        Assertions.assertEquals(list, actual);
    }

    @Test
    void testIsRepeatedShouldReturnTrue() {
        //given
        List<String> list = List.of(VALUE);
        RawConfigValueList underTest = new RawConfigValueList(CONFIG_PATH, list);

        //when
        final boolean actual = underTest.isRepeated();

        //then
        Assertions.assertTrue(actual);
    }
}
