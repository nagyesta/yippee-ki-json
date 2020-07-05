package com.github.nagyesta.yippeekijson.core.function.helper;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * {@link Function} for replacing {@link String} values using a RegExp .
 */
@Slf4j
public abstract class DecimalFunctionSupport implements Function<BigDecimal, BigDecimal> {

    private final BigDecimal operand;
    private final int scale;

    public DecimalFunctionSupport(@NonNull final BigDecimal operand, @NonNull final Integer scale) {
        this.operand = operand;
        this.scale = scale;
    }

    @Override
    public BigDecimal apply(final BigDecimal value) {
        if (value == null) {
            return null;
        }
        return getFunction().apply(value, operand).setScale(scale, RoundingMode.HALF_UP);
    }

    @NotNull
    protected abstract BiFunction<BigDecimal, BigDecimal, BigDecimal> getFunction();

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("operand=" + operand)
                .add("scale=" + scale)
                .toString();
    }
}
