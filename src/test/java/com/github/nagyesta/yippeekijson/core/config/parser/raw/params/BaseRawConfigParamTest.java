package com.github.nagyesta.yippeekijson.core.config.parser.raw.params;

import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BaseRawConfigParamTest {

    private static final String CONFIG_PATH = "configPath";
    private static final String VALUE = "value";

    private static Stream<Arguments> invalidSuitableForProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(true, true, false, false, false))
                .add(Arguments.of(false, false, false, true, false))
                .add(Arguments.of(false, false, false, true, false))
                .add(Arguments.of(false, false, true, true, true))
                .add(Arguments.of(false, false, true, false, true))
                .add(Arguments.of(false, false, true, false, false))
                .build();
    }

    private static Stream<Arguments> validSuitableForProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(true, true, true, true, true))
                .add(Arguments.of(true, true, true, false, true))
                .add(Arguments.of(true, false, true, true, false))
                .add(Arguments.of(true, false, true, false, false))
                .add(Arguments.of(false, true, false, false, true))
                .add(Arguments.of(false, false, false, false, false))
                .build();
    }

    private static Stream<Arguments> equalsProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(VALUE, VALUE, CONFIG_PATH, CONFIG_PATH, true))
                .add(Arguments.of(VALUE, VALUE.toUpperCase(), CONFIG_PATH, CONFIG_PATH, false))
                .add(Arguments.of(VALUE, VALUE, CONFIG_PATH, CONFIG_PATH.toUpperCase(), false))
                .add(Arguments.of(VALUE, VALUE.toUpperCase(), CONFIG_PATH, CONFIG_PATH.toUpperCase(), false))
                .add(Arguments.of(VALUE, null, CONFIG_PATH, null, false))
                .add(Arguments.of(null, VALUE, null, CONFIG_PATH, false))
                .build();
    }

    @ParameterizedTest
    @MethodSource("invalidSuitableForProvider")
    void testSuitableForShouldThrowExceptionWhenInputIsInvalid(final boolean configIsMap, final boolean configRepeated,
                                                               final boolean stringMap, final boolean paramMap, final boolean repeat) {
        //given
        final BaseRawConfigParam<String, String> underTest = underTestSpy(configIsMap, configRepeated);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.suitableFor(stringMap, paramMap, repeat));
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @MethodSource("validSuitableForProvider")
    void testSuitableForShouldReturnConvertedValueWhenInputIsValid(final boolean configIsMap, final boolean configRepeated,
                                                                   final boolean stringMap, final boolean paramMap, final boolean repeat) {
        //given
        final BaseRawConfigParam<String, String> underTest = underTestSpy(configIsMap, configRepeated);

        //when
        final Object actual = underTest.suitableFor(stringMap, paramMap, repeat);

        //then
        verify(underTest).suitableFor(eq(stringMap), eq(paramMap), eq(repeat));
        if (stringMap && repeat) {
            Class<?> toCheck = String.class;
            if (paramMap) {
                toCheck = RawConfigParam.class;
            }
            verify(underTest).asMaps();
            verify(underTest).asMap();
            Assertions.assertTrue(actual instanceof List);
            final List<?> list = (List<?>) actual;
            Assertions.assertTrue(list.get(0) instanceof Map);
            final List<Map<String, ?>> mapList = (List<Map<String, ?>>) list;
            Assertions.assertTrue(toCheck.isInstance(mapList.get(0).get(VALUE)));
        } else if (repeat) {
            verify(underTest).asStrings();
            verify(underTest).asString();
            Assertions.assertTrue(actual instanceof List);
        } else if (stringMap) {
            Class<?> toCheck = String.class;
            if (paramMap) {
                toCheck = RawConfigParam.class;
            }
            verify(underTest).asMap();
            verify(underTest, never()).asMaps();
            Assertions.assertTrue(actual instanceof Map);
            Assertions.assertTrue(toCheck.isInstance(((Map<String, ?>) actual).get(VALUE)));
        } else {
            verify(underTest).asString();
            verify(underTest, never()).asStrings();
            Assertions.assertTrue(actual instanceof String);
        }
    }

    @Test
    void testConvertValueOfEntryShouldWrapValidValues() {
        //given
        final BaseRawConfigParam<String, String> underTest = underTestSpy(true, true);

        //when
        final RawConfigParam actual = underTest.convertValueOfEntry(Map.entry(VALUE, VALUE));

        //then
        Assertions.assertTrue(actual instanceof RawConfigValue);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(VALUE, actual.asString());
    }

    @Test
    void testGetValueShouldReturnTheInputValue() {
        //given
        final BaseRawConfigParam<String, String> underTest = underTestSpy(false, false);

        //when
        final String actual = underTest.getValue();

        //then
        Assertions.assertEquals(VALUE, actual);
    }

    @Test
    void testGetConvertedShouldReturnTheConvertedValue() {
        //given
        final BaseRawConfigParam<String, String> underTest = underTestSpy(false, false);

        //when
        final String actual = underTest.getConverted();

        //then
        Assertions.assertEquals(VALUE.toUpperCase(), actual);
    }

    @ParameterizedTest
    @MethodSource("equalsProvider")
    void testEqualsShouldCheckConfigKeyAndValue(final String thisValue, final String thatValue,
                                                final String thisConfigKey, final String thatConfigKey,
                                                final boolean expected) {
        //given
        RawConfigValue a = new RawConfigValue(thisConfigKey, thisValue);
        RawConfigValue b = new RawConfigValue(thatConfigKey, thatValue);

        //when
        final boolean actual = a.equals(b);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("equalsProvider")
    void testHashCodeShouldCheckConfigKeyAndValue(final String thisValue, final String thatValue,
                                                  final String thisConfigKey, final String thatConfigKey,
                                                  final boolean expected) {
        //given
        RawConfigValue a = new RawConfigValue(thisConfigKey, thisValue);
        RawConfigValue b = new RawConfigValue(thatConfigKey, thatValue);

        //when
        final int actualA = a.hashCode();
        final int actualB = b.hashCode();

        //then
        if (expected) {
            Assertions.assertEquals(actualA, actualB);
        } else {
            Assertions.assertNotEquals(actualA, actualB);
        }
    }

    private BaseRawConfigParam<String, String> underTestSpy(final boolean configIsMap, final boolean configRepeated) {
        return spy(new BaseRawConfigParam<>(CONFIG_PATH, VALUE) {
            @Override
            protected String initConverted(@NotNull final String s) {
                return s.toUpperCase();
            }

            @Override
            public boolean isRepeated() {
                return configRepeated;
            }

            @Override
            public boolean isMapType() {
                return configIsMap;
            }

            @Override
            public String asString() {
                return VALUE;
            }

            @Override
            public Map<String, RawConfigParam> asMap() {
                return Map.of(VALUE, new RawConfigValue(CONFIG_PATH, VALUE));
            }
        });
    }
}
