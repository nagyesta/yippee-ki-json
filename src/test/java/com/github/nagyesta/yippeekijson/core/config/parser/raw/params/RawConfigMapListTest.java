package com.github.nagyesta.yippeekijson.core.config.parser.raw.params;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@LaunchAbortArmed
class RawConfigMapListTest {

    private static final String CONFIG_PATH = "config";
    private static final String VALUE = "value";
    private static final String KEY = "key";
    private static final Map<String, Object> VALUE_MAP = Map.of(KEY, VALUE);
    private static final Map<String, RawConfigParam> CONVERTED_VALUE = Map.of(KEY,
            new RawConfigValue(CONFIG_PATH + "." + KEY, VALUE));

    @Test
    void testInitConvertedShouldNotMakeTransformations() {
        //given
        RawConfigMapList underTest = new RawConfigMapList(CONFIG_PATH, List.of(VALUE_MAP));

        //when
        final List<Map<String, RawConfigParam>> actual = underTest.initConverted(List.of(VALUE_MAP));

        //then
        Assertions.assertEquals(List.of(CONVERTED_VALUE), actual);
    }

    @Test
    void testAsStringsShouldReturnTheConvertedCollection() {
        //given
        RawConfigMapList underTest = new RawConfigMapList(CONFIG_PATH, List.of(VALUE_MAP));

        //when
        final Collection<Map<String, RawConfigParam>> actual = underTest.asMaps();

        //then
        Assertions.assertEquals(List.of(CONVERTED_VALUE), actual);
    }

    @Test
    void testIsRepeatedShouldReturnTrue() {
        //given
        RawConfigMapList underTest = new RawConfigMapList(CONFIG_PATH, List.of(VALUE_MAP));

        //when
        final boolean actual = underTest.isRepeated();

        //then
        Assertions.assertTrue(actual);
    }

    @Test
    void testIsMapTypeShouldReturnTrue() {
        //given
        RawConfigMapList underTest = new RawConfigMapList(CONFIG_PATH, List.of(VALUE_MAP));

        //when
        final boolean actual = underTest.isMapType();

        //then
        Assertions.assertTrue(actual);
    }
}
