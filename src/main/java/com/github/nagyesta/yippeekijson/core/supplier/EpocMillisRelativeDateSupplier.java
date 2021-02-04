package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigInteger;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * {@link Supplier} returning a date time value relative to the current system date and time
 * using the epoch millis format.
 */
public final class EpocMillisRelativeDateSupplier implements Supplier<BigInteger> {

    static final String NAME = "epochMillisRelativeDate";

    private final Integer amount;
    private final ChronoUnit unit;
    private final long relativeTo;

    @SchemaDefinition(
            outputType = BigInteger.class,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_SUPPLIERS, section = "Epoch Millis Relative Date supplier"),
            sinceVersion = WikiConstants.VERSION_1_4_0,
            description = {
                    "This supplier returns a datetime value in the epoch millis format as",
                    "integer. The value will be calculated relative from a predefined time",
                    "(or the current time if not defined) using the configuration values",
                    "provided."
            },
            example = @Example(
                    in = "/examples/json/simple-accounts_in.json",
                    out = "/examples/json/simple-accounts_add-epochmillis_out.json",
                    yml = "/examples/yml/add-epochmillis-json.yml",
                    note = "This example will return 3 600 000 when the second supplier is called.")
    )
    @NamedSupplier(NAME)
    public EpocMillisRelativeDateSupplier(@ValueParam(docs = "The amount of time units we need to add to the current date time.")
                                          @NonNull final Integer amount,
                                          @ValueParam(docs = "The time unit we want to use to interpret the amount.")
                                          @NonNull final ChronoUnit unit,
                                          @ValueParam(docs = "The value we want to be relative to (in case it is not the current time).")
                                          @Nullable final BigInteger relativeTo) {
        this.amount = amount;
        this.unit = unit;
        this.relativeTo = Optional.ofNullable(relativeTo).map(BigInteger::longValue).orElse(Instant.now().toEpochMilli());
    }

    @Override
    public BigInteger get() {
        return BigInteger.valueOf(Instant.ofEpochMilli(relativeTo).plus(amount, unit).toEpochMilli());
    }

    long getRelativeTo() {
        return relativeTo;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EpocMillisRelativeDateSupplier.class.getSimpleName() + "[", "]")
                .add("amount=" + amount)
                .add("unit='" + unit.name() + "'")
                .add("relativeTo=" + relativeTo)
                .toString();
    }
}
