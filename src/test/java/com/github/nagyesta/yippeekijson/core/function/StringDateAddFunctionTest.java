package com.github.nagyesta.yippeekijson.core.function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

class StringDateAddFunctionTest {

    private static final Integer INT_42 = 42;
    private static final String YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String YYYY_MM_DD_T_HH_MM_SS_Z = YYYY_MM_DD_T_HH_MM_SS + "Z";
    private static final String DEC_03_2011_T_10_15_30 = "2011-12-03T10:15:30";
    private static final String DEC_03_2011_T_10_16_12 = "2011-12-03T10:16:12";
    private static final String DEC_03_2011_T_10_57_30 = "2011-12-03T10:57:30";
    private static final String GMT_5 = "+0500";
    private static final String GMT_1 = "+0100";

    private static Stream<Arguments> toStringProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(YYYY_MM_DD_T_HH_MM_SS_Z, INT_42, ChronoUnit.SECONDS))
                .add(Arguments.of(YYYY_MM_DD_T_HH_MM_SS_Z, INT_42, ChronoUnit.DAYS))
                .build();
    }

    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(YYYY_MM_DD_T_HH_MM_SS_Z, INT_42, ChronoUnit.SECONDS,
                        null, null))
                .add(Arguments.of(YYYY_MM_DD_T_HH_MM_SS_Z, INT_42, ChronoUnit.SECONDS,
                        DEC_03_2011_T_10_15_30 + GMT_1, DEC_03_2011_T_10_16_12 + GMT_1))
                .add(Arguments.of(YYYY_MM_DD_T_HH_MM_SS_Z, INT_42, ChronoUnit.MINUTES,
                        DEC_03_2011_T_10_15_30 + GMT_5, DEC_03_2011_T_10_57_30 + GMT_5))
                .add(Arguments.of(YYYY_MM_DD_T_HH_MM_SS, INT_42, ChronoUnit.SECONDS,
                        DEC_03_2011_T_10_15_30, DEC_03_2011_T_10_16_12))
                .add(Arguments.of(YYYY_MM_DD_T_HH_MM_SS, INT_42, ChronoUnit.MINUTES,
                        DEC_03_2011_T_10_15_30, DEC_03_2011_T_10_57_30))
                .build();
    }

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null, null))
                .add(Arguments.of(DateTimeFormatter.ISO_DATE_TIME.toFormat().toString(), null, null))
                .add(Arguments.of(null, INT_42, null))
                .add(Arguments.of(null, null, ChronoUnit.DAYS))
                .add(Arguments.of(null, INT_42, ChronoUnit.DAYS))
                .add(Arguments.of(YYYY_MM_DD_T_HH_MM_SS_Z, null, ChronoUnit.DAYS))
                .add(Arguments.of(YYYY_MM_DD_T_HH_MM_SS_Z, INT_42, null))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testApplyShouldWorkForValidInout(final String formatter, final Integer amount, final ChronoUnit unit,
                                          final String input, final String expected) {
        //given
        StringDateAddFunction underTest = new StringDateAddFunction(formatter, amount, unit);

        //when
        final String actual = underTest.apply(input);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldNotAllowNulls(final String formatter, final Integer amount, final ChronoUnit unit) {
        //given;
        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StringDateAddFunction(formatter, amount, unit));
    }

    @ParameterizedTest
    @MethodSource("toStringProvider")
    void testToStringShouldContainClassNameAndParameters(final String formatter, final Integer amount, final ChronoUnit unit) {
        //given
        final StringDateAddFunction underTest = new StringDateAddFunction(formatter, amount, unit);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(StringDateAddFunction.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(formatter));
        Assertions.assertTrue(actual.contains(String.valueOf(amount)));
        Assertions.assertTrue(actual.contains(unit.name()));
    }
}
