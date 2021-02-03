package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.function.helper.DecimalFunctionSupport;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.function.BiFunction;

/**
 * {@link java.util.function.Function} for subtraction of a decimal value using the parameter as first operand.
 */
@Slf4j
public final class DecimalSubtractFromFunction extends DecimalFunctionSupport {

    static final String NAME = "subtractFrom";

    @SchemaDefinition(
            inputType = BigDecimal.class,
            outputType = BigDecimal.class,
            sinceVersion = WikiConstants.VERSION_1_4_0,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_FUNCTIONS, section = "Decimal subtract from function"),
            description = {
                    "This function subtracts input value from the value provided as \"operand\" then adjusts the \"scale\" as defined."
            },
            example = @Example(
                    in = "/examples/json/blog-entries_in.json",
                    out = "/examples/json/blog-entries_subtract-likes-from-100_out.json",
                    yml = "/examples/yml/calculate-subtract-from-100.yml",
                    note = "In this example we subtracted the previous value from 100 using this function."
            )
    )
    @NamedFunction(NAME)
    public DecimalSubtractFromFunction(@ValueParam(docs = "First operand of the calculation.")
                                       @NotNull final BigDecimal operand,
                                       @ValueParam(docs = "The number of digits we want to keep right of the decimal point.")
                                       @NotNull final Integer scale) {
        super(operand, scale);
    }

    @Override
    @NotNull
    protected BiFunction<BigDecimal, BigDecimal, BigDecimal> getFunction() {
        //Swaps the order of the numbers
        return (op1, op2) -> op2.subtract(op1);
    }
}
