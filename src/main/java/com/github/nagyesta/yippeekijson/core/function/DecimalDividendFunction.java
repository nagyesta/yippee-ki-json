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
import java.math.RoundingMode;
import java.util.function.BiFunction;

/**
 * {@link java.util.function.Function} for division of a decimal value by the node value.
 */
@Slf4j
public final class DecimalDividendFunction extends DecimalFunctionSupport {

    static final String NAME = "dividend";

    private final int scale;

    @SchemaDefinition(
            inputType = BigDecimal.class,
            outputType = BigDecimal.class,
            sinceVersion = WikiConstants.VERSION_1_4_0,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_FUNCTIONS, section = "Decimal dividend function"),
            description = {
                    "This function divides the value provided as \"operand\" with the input value, then adjusts the \"scale\" as defined."
            },
            example = @Example(
                    in = "/examples/json/blog-entries_in.json",
                    out = "/examples/json/blog-entries_divide-by-likes_out.json",
                    yml = "/examples/yml/calculate-divide-by-likes.yml",
                    note = "In this example we have divided 600 by the number of likes."
            )
    )
    @NamedFunction(NAME)
    public DecimalDividendFunction(@ValueParam(docs = "First operand of the calculation.")
                                   @NotNull final BigDecimal operand,
                                   @ValueParam(docs = "The number of digits we want to keep right of the decimal point.")
                                   @NotNull final Integer scale) {
        super(operand, scale);
        this.scale = scale;
    }

    @Override
    @NotNull
    protected BiFunction<BigDecimal, BigDecimal, BigDecimal> getFunction() {
        //Swaps the order of the numbers
        return (op1, op2) -> op2.divide(op1, scale, RoundingMode.HALF_UP);
    }
}
