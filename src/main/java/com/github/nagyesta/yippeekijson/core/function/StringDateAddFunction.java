package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.helper.DateFormatHelper;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * {@link Function} for adding a certain amount of time to a value formatted as {@link String}.
 */
@Slf4j
public final class StringDateAddFunction implements Function<String, String> {

    static final String NAME = "stringDateAdd";

    private final DateFormatHelper helper;
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
        this.amount = amount;
        this.unit = unit;
        this.helper = new DateFormatHelper(DateTimeFormatter.ofPattern(formatter));
    }

    @Override
    public String apply(final String date) {
        return helper.parseAneAdjust(date, amount, unit);
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
