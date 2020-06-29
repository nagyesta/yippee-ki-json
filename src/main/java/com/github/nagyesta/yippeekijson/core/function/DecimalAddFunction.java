package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.function.helper.DecimalFunctionSupport;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.function.BiFunction;

/**
 * {@link java.util.function.Function} for addition of a decimal value.
 */
@Slf4j
public final class DecimalAddFunction extends DecimalFunctionSupport {

    static final String NAME = "add";
    static final String PARAM_OPERAND = "operand";
    static final String PARAM_SCALE = "scale";

    @NamedFunction(NAME)
    public DecimalAddFunction(@NotNull @ValueParam(PARAM_OPERAND) final String operand,
                              @NotNull @ValueParam(PARAM_SCALE) final String scale) {
        super(operand, scale);
    }

    @Override
    @NotNull
    protected BiFunction<BigDecimal, BigDecimal, BigDecimal> getFunction() {
        return BigDecimal::add;
    }
}
