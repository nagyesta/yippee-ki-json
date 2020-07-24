package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * {@link Function} for adding a certain amount of time to a value representing UTC time using epoch millis.
 */
@Slf4j
public final class EpochMilliDateAddFunction implements Function<BigDecimal, BigDecimal> {

    static final String NAME = "epochMillisDateAdd";

    private final int amount;
    private final ChronoUnit unit;

    @SchemaDefinition(
            inputType = BigDecimal.class,
            outputType = BigDecimal.class,
            sinceVersion = WikiConstants.VERSION_1_1_0,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_FUNCTIONS, section = "Epoch millis date add function"),
            description = {
                    "This function parses the input value as a date time using the assumption that the number is",
                    "representing an instant using the epoch millis approach, then adds the necessary \"amount\"",
                    "of time \"unit\"s before converting it back the same way."
            },
            example = @Example(
                    in = "/examples/json/epoch-date_in.json",
                    out = "/examples/json/epoch-date_out.json",
                    yml = "/examples/yml/epoch-date.yml",
                    note = "In this example we have changed the expiration date of an account by adding 1 hour."
            )
    )
    @NamedFunction(NAME)
    public EpochMilliDateAddFunction(@ValueParam(docs = "The amount of time units we need to add to the date time.")
                                     @NonNull final Integer amount,
                                     @ValueParam(docs = "The time unit we want to use to interpret the amount.")
                                     @NonNull final ChronoUnit unit) {
        this.amount = amount;
        this.unit = unit;
    }

    @Override
    public BigDecimal apply(final BigDecimal date) {
        if (date == null) {
            return null;
        }
        return BigDecimal.valueOf(Instant.ofEpochMilli(date.longValue()).plus(amount, unit).toEpochMilli());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EpochMilliDateAddFunction.class.getSimpleName() + "[", "]")
                .add("amount=" + amount)
                .add("unit=" + unit.name())
                .toString();
    }
}
