package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.MethodParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.function.BiFunction;

/**
 * {@link java.util.function.Function} for multiplying a decimal value.
 */
@Slf4j
public final class DecimalMultiplyFunction extends DecimalFunction {

    static final String NAME = "multiply";
    static final String PARAM_OPERAND = "operand";
    static final String PARAM_SCALE = "scale";

    @NamedFunction(NAME)
    public DecimalMultiplyFunction(@NotNull @MethodParam(PARAM_OPERAND) final String operand,
                                   @NotNull @MethodParam(PARAM_SCALE) final String scale) {
        super(operand, scale);
    }

    @Override
    @NotNull
    protected BiFunction<BigDecimal, BigDecimal, BigDecimal> getFunction() {
        return BigDecimal::multiply;
    }
}
