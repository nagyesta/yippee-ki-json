package com.github.nagyesta.yippeekijson.core.function.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.stream.Stream;

class ChronoUnitSupportTest {

    private static Stream<Arguments> validUnitProvider() {
        return Arrays.stream(ChronoUnit.values())
                .map(chronoUnit -> {
                    if (chronoUnit.ordinal() % 2 == 0) {
                        return chronoUnit.name();
                    } else {
                        return chronoUnit.name().toLowerCase();
                    }
                })
                .map(Arguments::of);
    }

    private static Stream<Arguments> invalidUnitProvider() {
        return Arrays.stream(ChronoUnit.values())
                .map(chronoUnit -> chronoUnit.name() + "-1")
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("validUnitProvider")
    void testToChronoUnitShouldWorkForValidUnits(final String unit) {
        //given
        ChronoUnitSupport underTest = new ChronoUnitSupport();

        //when
        final ChronoUnit actual = underTest.toChronoUnit(unit);

        //then
        Assertions.assertEquals(unit.toUpperCase(), actual.name());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @MethodSource("invalidUnitProvider")
    void testToChronoUnitShouldThrowExceptionForInvalidUnits(final String unit) {
        //given
        ChronoUnitSupport underTest = new ChronoUnitSupport();

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.toChronoUnit(unit));
    }
}
