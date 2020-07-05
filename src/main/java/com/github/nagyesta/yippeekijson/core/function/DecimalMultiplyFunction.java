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
 * {@link java.util.function.Function} for multiplying a decimal value.
 */
@Slf4j
public final class DecimalMultiplyFunction extends DecimalFunctionSupport {

    static final String NAME = "multiply";

    @SchemaDefinition(
            inputType = BigDecimal.class,
            outputType = BigDecimal.class,
            sinceVersion = WikiConstants.VERSION_1_1_0,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_FUNCTIONS, section = "Decimal multiply function"),
            description = {
                    "This function multiplies the input value with the value provided as \"operand\", then adjusts",
                    "the \"scale\" as defined."
            },
            example = @Example(
                    in = "/examples/json/blog-entries_in.json",
                    out = "/examples/json/blog-entries_double-likes_out.json",
                    yml = "/examples/yml/calculate-multiply-likes.yml",
                    note = "In this example we have doubled the number of likes using this function."
            )
    )
    @NamedFunction(NAME)
    public DecimalMultiplyFunction(@ValueParam(docs = "Second operand of the calculation.")
                                   @NotNull final BigDecimal operand,
                                   @ValueParam(docs = "The number of digits we want to keep right of the decimal point.")
                                   @NotNull final Integer scale) {
        super(operand, scale);
    }

    @Override
    @NotNull
    protected BiFunction<BigDecimal, BigDecimal, BigDecimal> getFunction() {
        return BigDecimal::multiply;
    }
}
