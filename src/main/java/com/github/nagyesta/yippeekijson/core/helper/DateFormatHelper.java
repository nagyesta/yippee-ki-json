package com.github.nagyesta.yippeekijson.core.helper;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalUnit;
import java.util.function.Function;

/**
 * Helper for date formatting related operations.
 */
public class DateFormatHelper {

    private final DateTimeFormatter dateTimeFormatter;

    /**
     * Creates a new version and sets the formatter.
     *
     * @param dateTimeFormatter The formatter.
     */
    public DateFormatHelper(final DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    /**
     * Parses the date and adjusts the value with the given amount and temporal unit before formatting it again.
     *
     * @param date   The date represented as String.
     * @param amount The amount of units we want to adjust the date value.
     * @param unit   The temporal unit we want to adjust by.
     * @return The formatted version of the adjusted date.
     */
    public String parseAneAdjust(final String date, final int amount, final TemporalUnit unit) {
        if (date == null) {
            return null;
        }
        final TemporalAccessor parse = dateTimeFormatter.parse(date);
        if (parse.isSupported(ChronoField.OFFSET_SECONDS)) {
            return adjustDate(date, OffsetDateTime.class, s -> OffsetDateTime.parse(s, dateTimeFormatter), amount, unit)
                    .format(dateTimeFormatter);
        } else if (parse.isSupported(ChronoField.HOUR_OF_DAY)) {
            return adjustDate(date, LocalDateTime.class, s -> LocalDateTime.parse(s, dateTimeFormatter), amount, unit)
                    .format(dateTimeFormatter);
        } else {
            return adjustDate(date, LocalDate.class, s -> LocalDate.parse(s, dateTimeFormatter), amount, unit)
                    .format(dateTimeFormatter);
        }
    }

    @NotNull
    private <T extends Temporal> T adjustDate(final String date, final Class<T> type, final Function<String, T> parse,
                                              final int amount, final TemporalUnit unit) {
        final T offsetDateTime = parse.apply(date);
        return type.cast(offsetDateTime.plus(amount, unit));
    }
}
