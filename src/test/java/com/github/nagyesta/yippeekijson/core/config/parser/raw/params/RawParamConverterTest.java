package com.github.nagyesta.yippeekijson.core.config.parser.raw.params;

import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

class RawParamConverterTest {

    private static final String CONFIG_PATH = "config";
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final Map<String, Object> VALUE_MAP = Map.of(KEY, VALUE);
    private static final int INT_42 = 42;
    private static final BigInteger BIG_INT_42 = new BigInteger("42");

    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(VALUE, new RawConfigValue(CONFIG_PATH, VALUE)))
                .add(Arguments.of(VALUE_MAP, new RawConfigMap(CONFIG_PATH, VALUE_MAP)))
                .add(Arguments.of(List.of(VALUE), new RawConfigValueList(CONFIG_PATH, List.of(VALUE))))
                .add(Arguments.of(List.of(VALUE_MAP), new RawConfigMapList(CONFIG_PATH, List.of(VALUE_MAP))))
                .build();
    }

    private static Stream<Arguments> invalidInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, IllegalArgumentException.class))
                .add(Arguments.of(Collections.emptyMap(), IllegalArgumentException.class))
                .add(Arguments.of(Collections.emptyList(), IllegalArgumentException.class))
                .add(Arguments.of(BIG_INT_42, IllegalArgumentException.class))
                .add(Arguments.of(Set.of(VALUE_MAP), IllegalArgumentException.class))
                .add(Arguments.of(List.of(VALUE_MAP, VALUE), IllegalArgumentException.class))
                .add(Arguments.of(List.of(INT_42), IllegalArgumentException.class))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testApplyShouldConvertValidInput(final Object input, final RawConfigParam expected) {
        //given
        RawParamConverter underTest = new RawParamConverter(CONFIG_PATH);

        //when
        final RawConfigParam actual = underTest.apply(input);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("invalidInputProvider")
    void testApplyShouldFailForInvalidInput(final Object input, final Class<Exception> expected) {
        //given
        RawParamConverter underTest = new RawParamConverter(CONFIG_PATH);

        //when + then exception
        Assertions.assertThrows(expected, () -> underTest.apply(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testConstructorShouldThrowExceptionForNullAndBlank(final String input) {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new RawParamConverter(input));
    }
}
