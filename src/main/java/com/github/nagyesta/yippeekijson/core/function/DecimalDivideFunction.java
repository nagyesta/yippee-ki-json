package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.function.helper.DecimalFunctionSupport;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BiFunction;

/**
 * {@link java.util.function.Function} for division of a decimal value.
 */
@Slf4j
public final class DecimalDivideFunction extends DecimalFunctionSupport {

    static final String NAME = "divide";
    static final String PARAM_OPERAND = "operand";
    static final String PARAM_SCALE = "scale";

    private final int scale;

    @NamedFunction(NAME)
    public DecimalDivideFunction(@NotNull @ValueParam(PARAM_OPERAND) final String operand,
                                 @NotNull @ValueParam(PARAM_SCALE) final String scale) {
        super(operand, scale);
        this.scale = Integer.parseInt(scale);
    }

    @Override
    @NotNull
    protected BiFunction<BigDecimal, BigDecimal, BigDecimal> getFunction() {
        return (op1, op2) -> op1.divide(op2, scale, RoundingMode.HALF_UP);
    }
}
