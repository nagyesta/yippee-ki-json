package com.github.nagyesta.yippeekijson.core.config.parser.raw.params;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.spy;

@LaunchAbortArmed
class BaseRawConfigParamTest {

    private static final String CONFIG_PATH = "configPath";
    private static final String VALUE = "value";

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

    @SuppressWarnings({"EqualsWithItself", "ConstantConditions"})
    @Test
    void testEqualsShouldCheckSameReference() {
        //given
        RawConfigValue a = new RawConfigValue(CONFIG_PATH, CONFIG_PATH);

        //when
        final boolean actual = a.equals(a);

        //then
        Assertions.assertTrue(actual);
    }

    @Test
    void testEqualsShouldCheckDifferentClass() {
        //given
        RawConfigValue a = new RawConfigValue(CONFIG_PATH, CONFIG_PATH);

        //when
        final boolean actual = a.equals(new Object());

        //then
        Assertions.assertFalse(actual);
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
