package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.MethodParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.function.helper.ChronoUnitSupport;
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
public final class EpochMilliDateAddFunction extends ChronoUnitSupport implements Function<BigDecimal, BigDecimal> {

    static final String NAME = "epochMillisDateAdd";
    static final String PARAM_AMOUNT = "amount";
    static final String PARAM_UNIT = "unit";

    private final int amount;
    private final ChronoUnit unit;

    @NamedFunction(NAME)
    public EpochMilliDateAddFunction(@MethodParam(PARAM_AMOUNT) @NonNull final String amount,
                                     @MethodParam(PARAM_UNIT) @NonNull final String unit) {
        this.amount = Integer.parseInt(amount);
        this.unit = toChronoUnit(unit);
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
