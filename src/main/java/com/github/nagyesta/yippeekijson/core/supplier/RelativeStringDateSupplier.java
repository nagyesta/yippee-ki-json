package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.helper.DateFormatHelper;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * {@link Supplier} returning a date time value relative to the current system date and time
 * using the epoch millis format.
 */
public final class RelativeStringDateSupplier implements Supplier<String> {

    static final String NAME = "relativeStringDate";

    private final Integer amount;
    private final ChronoUnit unit;
    private final String relativeTo;
    private final String formatterPattern;
    private final DateFormatHelper helper;

    @SchemaDefinition(
            outputType = String.class,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_SUPPLIERS, section = "Relative String Date supplier"),
            sinceVersion = WikiConstants.VERSION_1_4_0,
            description = {
                    "This supplier returns a datetime value in the String formatted way",
                    "using the format provided. The value will be calculated relative from",
                    "a predefined time (or the current time if not defined) using the",
                    "configuration values provided."
            },
            example = @Example(
                    in = "/examples/json/simple-accounts_in.json",
                    out = "/examples/json/simple-accounts_add-stringdate_out.json",
                    yml = "/examples/yml/add-stringdate-json.yml",
                    note = "This example will return '2020-01-02' when the second supplier is called.")
    )
    @NamedSupplier(NAME)
    public RelativeStringDateSupplier(@ValueParam(docs = "The format String we need to use for date time parsing. "
            + "[See](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html)")
                                      @NonNull final String formatter,
                                      @ValueParam(docs = "The amount of time units we need to add to the current date time.")
                                      @NonNull final Integer amount,
                                      @ValueParam(docs = "The time unit we want to use to interpret the amount.")
                                      @NonNull final ChronoUnit unit,
                                      @ValueParam(docs = "The value we want to be relative to (in case it is not the current time).")
                                      @Nullable final String relativeTo) {
        this.formatterPattern = formatter;
        this.amount = amount;
        this.unit = unit;
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formatter);
        this.relativeTo = Optional.ofNullable(relativeTo).orElse(dateTimeFormatter.format(ZonedDateTime.now()));
        this.helper = new DateFormatHelper(dateTimeFormatter);
    }

    @Override
    public String get() {
        return helper.parseAneAdjust(relativeTo, amount, unit);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RelativeStringDateSupplier.class.getSimpleName() + "[", "]")
                .add("format='" + formatterPattern + "'")
                .add("amount=" + amount)
                .add("unit='" + unit.name() + "'")
                .add("relativeTo='" + relativeTo + "'")
                .toString();
    }
}
