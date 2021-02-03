package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

@LaunchAbortArmed
class RelativeStringDateSupplierTest {

    private static final Integer INT_42 = 42;
    private static final String YYYY_MM_DD_T_HH = "yyyy-MM-dd'T'HH";
    private static final String YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String YYYY_MM_DD_T_HH_MM_SS_Z = YYYY_MM_DD_T_HH_MM_SS + "Z";
    private static final String DEC_03_2011_T_10_15_30 = "2011-12-03T10:15:30";
    private static final String DEC_03_2011_T_10_16_12 = "2011-12-03T10:16:12";
    private static final String DEC_03_2011_T_10_57_30 = "2011-12-03T10:57:30";
    private static final String GMT_5 = "+0500";
    private static final String GMT_1 = "+0100";

    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(YYYY_MM_DD_T_HH_MM_SS, INT_42, ChronoUnit.SECONDS,
                        DEC_03_2011_T_10_15_30, DEC_03_2011_T_10_16_12))
                .add(Arguments.of(YYYY_MM_DD_T_HH_MM_SS_Z, INT_42, ChronoUnit.SECONDS,
                        DEC_03_2011_T_10_15_30 + GMT_1, DEC_03_2011_T_10_16_12 + GMT_1))
                .add(Arguments.of(YYYY_MM_DD_T_HH_MM_SS, -INT_42, ChronoUnit.MINUTES,
                        DEC_03_2011_T_10_57_30, DEC_03_2011_T_10_15_30))
                .add(Arguments.of(YYYY_MM_DD_T_HH_MM_SS_Z, -INT_42, ChronoUnit.MINUTES,
                        DEC_03_2011_T_10_57_30 + GMT_5, DEC_03_2011_T_10_15_30 + GMT_5))
                .add(Arguments.of(YYYY_MM_DD_T_HH, -1, ChronoUnit.DAYS, null,
                        ZonedDateTime.now().minus(1, ChronoUnit.DAYS)
                                .format(DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH))))
                .build();
    }

    private static Stream<Arguments> invalidInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null, null, null))
                .add(Arguments.of(YYYY_MM_DD_T_HH_MM_SS, null, null, null))
                .add(Arguments.of(null, 1, null, null))
                .add(Arguments.of(null, null, ChronoUnit.HOURS, null))
                .add(Arguments.of(null, null, null, DEC_03_2011_T_10_16_12))
                .add(Arguments.of(null, 2, ChronoUnit.HOURS, DEC_03_2011_T_10_16_12))
                .add(Arguments.of(YYYY_MM_DD_T_HH_MM_SS, null, ChronoUnit.HOURS, DEC_03_2011_T_10_16_12))
                .add(Arguments.of(YYYY_MM_DD_T_HH_MM_SS, 1, null, DEC_03_2011_T_10_16_12))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testGetShouldReturnTheStaticString(final String format, final Integer amount, final ChronoUnit unit,
                                            final String relativeTo, final String expected) {
        //given
        final RelativeStringDateSupplier underTest = new RelativeStringDateSupplier(format, amount, unit, relativeTo);

        //when
        final String actual = underTest.get();

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("invalidInputProvider")
    void testConstructorShouldNotAllowNulls(final String format, final Integer amount, final ChronoUnit unit, final String relativeTo) {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new RelativeStringDateSupplier(format, amount, unit, relativeTo));
    }

    @SuppressWarnings("unused")
    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testToStringShouldContainClassNameAndInputs(final String format, final Integer amount, final ChronoUnit unit,
                                                     final String relativeTo, final String ignored) {
        //given
        final RelativeStringDateSupplier underTest = new RelativeStringDateSupplier(format, amount, unit, relativeTo);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(RelativeStringDateSupplier.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(format));
        Assertions.assertTrue(actual.contains(String.valueOf(amount)));
        Assertions.assertTrue(actual.contains(unit.name()));
        if (relativeTo != null) {
            Assertions.assertTrue(actual.contains(relativeTo));
        }
    }
}
