package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

@LaunchAbortArmed
class EpocMillisRelativeDateSupplierTest {

    @SuppressWarnings("checkstyle:MagicNumber")
    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(30, ChronoUnit.MINUTES, BigInteger.valueOf(100L), BigInteger.valueOf(1800100L)))
                .add(Arguments.of(-1, ChronoUnit.HOURS, null, null))
                .add(Arguments.of(25, ChronoUnit.SECONDS, null, null))
                .add(Arguments.of(25, ChronoUnit.MILLIS, BigInteger.valueOf(50L), BigInteger.valueOf(75L)))
                .build();
    }

    private static Stream<Arguments> invalidInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null, null))
                .add(Arguments.of(1, null, null))
                .add(Arguments.of(null, ChronoUnit.HOURS, null))
                .add(Arguments.of(null, null, BigInteger.ZERO))
                .add(Arguments.of(null, ChronoUnit.HOURS, BigInteger.ZERO))
                .add(Arguments.of(1, null, BigInteger.ZERO))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testGetShouldReturnTheStaticString(final Integer amount, final ChronoUnit unit,
                                            final BigInteger relativeTo, final BigInteger expected) {
        //given
        final EpocMillisRelativeDateSupplier underTest = new EpocMillisRelativeDateSupplier(amount, unit, relativeTo);

        //when
        final BigInteger actual = underTest.get();

        //then
        if (expected == null) {
            final long expectedLong = underTest.getRelativeTo() + unit.getDuration().toMillis() * amount;
            Assertions.assertEquals(expectedLong, actual.longValue());
        } else {
            Assertions.assertEquals(expected, actual);
        }
    }

    @ParameterizedTest
    @MethodSource("invalidInputProvider")
    void testConstructorShouldNotAllowNulls(final Integer amount, final ChronoUnit unit, final BigInteger relativeTo) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new EpocMillisRelativeDateSupplier(amount, unit, relativeTo));
    }

    @SuppressWarnings("unused")
    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testToStringShouldContainClassNameAndInputs(final Integer amount, final ChronoUnit unit,
                                                     final BigInteger relativeTo, final BigInteger ignored) {
        //given
        final EpocMillisRelativeDateSupplier underTest = new EpocMillisRelativeDateSupplier(amount, unit, relativeTo);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(EpocMillisRelativeDateSupplier.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(String.valueOf(amount)));
        Assertions.assertTrue(actual.contains(unit.name()));
        if (relativeTo == null) {
            Assertions.assertTrue(actual.contains(String.valueOf(underTest.getRelativeTo())));
        } else {
            Assertions.assertTrue(actual.contains(String.valueOf(relativeTo)));
        }
    }
}
