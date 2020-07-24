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
 * {@link java.util.function.Function} for addition of a decimal value.
 */
@Slf4j
public final class DecimalAddFunction extends DecimalFunctionSupport {

    static final String NAME = "add";

    @SchemaDefinition(
            inputType = BigDecimal.class,
            outputType = BigDecimal.class,
            sinceVersion = WikiConstants.VERSION_1_1_0,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_FUNCTIONS, section = "Decimal add function"),
            description = {
                    "This function adds the value provided as \"operand\" to the input value then adjusts the \"scale\" as defined."
            },
            example = @Example(
                    in = "/examples/json/blog-entries_in.json",
                    out = "/examples/json/blog-entries_add-views_out.json",
                    yml = "/examples/yml/calculate-add-view.yml",
                    note = "In this example we have increased the number of views using this function."
            )
    )
    @NamedFunction(NAME)
    public DecimalAddFunction(@ValueParam(docs = "Second operand of the calculation.")
                              @NotNull final BigDecimal operand,
                              @ValueParam(docs = "The number of digits we want to keep right of the decimal point.")
                              @NotNull final Integer scale) {
        super(operand, scale);
    }

    @Override
    @NotNull
    protected BiFunction<BigDecimal, BigDecimal, BigDecimal> getFunction() {
        return BigDecimal::add;
    }
}
