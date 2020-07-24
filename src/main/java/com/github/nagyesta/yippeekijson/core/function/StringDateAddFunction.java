package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
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
public final class StringDateAddFunction implements Function<String, String> {

    static final String NAME = "stringDateAdd";

    private final DateTimeFormatter dateTimeFormatter;
    private final String formatterPattern;
    private final int amount;
    private final ChronoUnit unit;

    @SchemaDefinition(
            inputType = String.class,
            outputType = String.class,
            sinceVersion = WikiConstants.VERSION_1_1_0,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_FUNCTIONS, section = "String date add function"),
            description = {
                    "This function parses the input value using the \"formatter\" pattern, then adds the necessary",
                    "\"amount\" of time \"unit\"s before formatting it using the same pattern."
            },
            example = @Example(
                    in = "/examples/json/string-date_in.json",
                    out = "/examples/json/string-date_out.json",
                    yml = "/examples/yml/string-date.yml",
                    note = "In this example we have changed the expiration date of an account by adding 6 months."
            )
    )
    @NamedFunction(NAME)
    public StringDateAddFunction(
            @ValueParam(docs = "The format String we need to use for date time parsing. "
                    + "[See](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html)")
            @NonNull final String formatter,
            @ValueParam(docs = "The amount of time units we need to add to the date time.")
            @NonNull final Integer amount,
            @ValueParam(docs = "The time unit we want to use to interpret the amount.")
            @NonNull final ChronoUnit unit) {
        this.formatterPattern = formatter;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(formatter);
        this.amount = amount;
        this.unit = unit;
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
        } else if (parse.isSupported(ChronoField.HOUR_OF_DAY)) {
            return adjustDate(date, LocalDateTime.class, s -> LocalDateTime.parse(s, dateTimeFormatter))
                    .format(dateTimeFormatter);
        } else {
            return adjustDate(date, LocalDate.class, s -> LocalDate.parse(s, dateTimeFormatter))
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
