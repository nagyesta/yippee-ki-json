package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

@LaunchAbortArmed
class EpochMilliDateAddFunctionTest {

    private static final int INT_42 = 42;

    private static Stream<Arguments> toStringProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(INT_42, ChronoUnit.SECONDS))
                .add(Arguments.of(INT_42, ChronoUnit.DAYS))
                .build();
    }

    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(INT_42, ChronoUnit.SECONDS, null, null))
                .add(Arguments.of(INT_42, ChronoUnit.SECONDS, new BigDecimal("10000.2"), new BigDecimal("52000")))
                .build();
    }

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(INT_42, null))
                .add(Arguments.of(null, ChronoUnit.DAYS))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testApplyShouldWorkForValidInout(final Integer amount, final ChronoUnit unit,
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
    void testConstructorShouldNotAllowNulls(final Integer amount, final ChronoUnit unit) {
        //given;
        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new EpochMilliDateAddFunction(amount, unit));
    }

    @ParameterizedTest
    @MethodSource("toStringProvider")
    void testToStringShouldContainClassNameAndParameters(final Integer amount, final ChronoUnit unit) {
        //given
        final EpochMilliDateAddFunction underTest = new EpochMilliDateAddFunction(amount, unit);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(EpochMilliDateAddFunction.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(String.valueOf(amount)));
        Assertions.assertTrue(actual.contains(unit.name()));
    }
}
