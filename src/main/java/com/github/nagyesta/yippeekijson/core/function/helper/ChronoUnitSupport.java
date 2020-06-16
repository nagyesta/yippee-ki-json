package com.github.nagyesta.yippeekijson.core.function.helper;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public class ChronoUnitSupport {

    /**
     * Converts a {@link String} value to a {@link ChronoUnit} case insensitive.
     * Throws {@link IllegalArgumentException} if the unit is not found.
     *
     * @param unit the {@link ChronoUnit} enum value we are looking for.
     * @return The converted enum value
     */
    protected ChronoUnit toChronoUnit(final String unit) {
        return Arrays.stream(ChronoUnit.values())
                .filter(c -> c.name().equalsIgnoreCase(unit))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown ChronoUnit supplied: " + unit));
    }
}
