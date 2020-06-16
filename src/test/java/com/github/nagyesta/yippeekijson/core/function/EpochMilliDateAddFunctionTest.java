package com.github.nagyesta.yippeekijson.core.function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

class EpochMilliDateAddFunctionTest {

    private static final String INT_42 = "42";

    private static Stream<Arguments> toStringProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(INT_42, ChronoUnit.SECONDS.name()))
                .add(Arguments.of(INT_42, ChronoUnit.DAYS.name()))
                .build();
    }

    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(INT_42, ChronoUnit.SECONDS.name(), null, null))
                .add(Arguments.of(INT_42, ChronoUnit.SECONDS.name(), new BigDecimal("10000.2"), new BigDecimal("52000")))
                .build();
    }

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(INT_42, null))
                .add(Arguments.of(null, ChronoUnit.DAYS.name()))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testApplyShouldWorkForValidInout(final String amount, final String unit,
                                          final BigDecimal input, final BigDecimal expected) {
        //given
        EpochMilliDateAddFunction underTest = new EpochMilliDateAddFunction(amount, unit);

        //when
        final BigDecimal actual = underTest.apply(input);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldNotAllowNulls(final String amount, final String unit) {
        //given;
        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new EpochMilliDateAddFunction(amount, unit));
    }

    @ParameterizedTest
    @MethodSource("toStringProvider")
    void testToStringShouldContainClassNameAndParameters(final String amount, final String unit) {
        //given
        final EpochMilliDateAddFunction underTest = new EpochMilliDateAddFunction(amount, unit);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(EpochMilliDateAddFunction.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(amount));
        Assertions.assertTrue(actual.contains(unit));
    }
}
