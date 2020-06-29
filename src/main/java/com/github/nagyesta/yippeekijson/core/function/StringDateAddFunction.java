package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.function.helper.ChronoUnitSupport;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * {@link Function} for adding a certain amount of time to a value formatted as {@link String}.
 */
@Slf4j
public final class StringDateAddFunction extends ChronoUnitSupport implements Function<String, String> {

    static final String NAME = "stringDateAdd";
    static final String PARAM_FORMATTER = "formatter";
    static final String PARAM_AMOUNT = "amount";
    static final String PARAM_UNIT = "unit";

    private final DateTimeFormatter dateTimeFormatter;
    private final String formatterPattern;
    private final int amount;
    private final ChronoUnit unit;

    @NamedFunction(NAME)
    public StringDateAddFunction(@ValueParam(PARAM_FORMATTER) @NonNull final String formatter,
                                 @ValueParam(PARAM_AMOUNT) @NonNull final String amount,
                                 @ValueParam(PARAM_UNIT) @NonNull final String unit) {
        this.formatterPattern = formatter;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(formatter);
        this.amount = Integer.parseInt(amount);
        this.unit = toChronoUnit(unit);
    }

    @Override
    public String apply(final String date) {
        if (date == null) {
            return null;
        }
        final TemporalAccessor parse = dateTimeFormatter.parse(date);
        if (parse.isSupported(ChronoField.OFFSET_SECONDS)) {
            return adjustDate(date, OffsetDateTime.class, s -> OffsetDateTime.parse(s, dateTimeFormatter))
                    .format(dateTimeFormatter);
        } else {
            return adjustDate(date, LocalDateTime.class, s -> LocalDateTime.parse(s, dateTimeFormatter))
                    .format(dateTimeFormatter);
        }
    }

    @NotNull
    private <T extends Temporal> T adjustDate(final String date, final Class<T> type, final Function<String, T> parse) {
        final T offsetDateTime = parse.apply(date);
        return type.cast(offsetDateTime.plus(amount, unit));
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StringDateAddFunction.class.getSimpleName() + "[", "]")
                .add("dateTimeFormatter='" + formatterPattern + "'")
                .add("amount=" + amount)
                .add("unit=" + unit.name())
                .toString();
    }
}
