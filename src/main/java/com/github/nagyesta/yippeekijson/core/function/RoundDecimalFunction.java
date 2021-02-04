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
 * {@link java.util.function.Function} for calculating the rounded value of the node value.
 */
@Slf4j
public final class RoundDecimalFunction extends DecimalFunctionSupport {

    static final String NAME = "roundDecimal";

    private final int scale;

    @SchemaDefinition(
            inputType = BigDecimal.class,
            outputType = BigDecimal.class,
            sinceVersion = WikiConstants.VERSION_1_4_0,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_FUNCTIONS, section = "Round decimal function"),
            description = {
                    "This function parses the input value, then adjusts the \"scale\" as defined."
            },
            example = @Example(
                    in = "/examples/json/distances_in.json",
                    out = "/examples/json/distances_round_out.json",
                    yml = "/examples/yml/distances_round.yml",
                    note = "In this example we have replaced all numbers with their rounded values."
            )
    )
    @NamedFunction(NAME)
    public RoundDecimalFunction(@ValueParam(docs = "The number of digits we want to keep right of the decimal point.")
                                @NotNull final Integer scale) {
        super(BigDecimal.ZERO, scale);
        this.scale = scale;
    }

    @Override
    @NotNull
    protected BiFunction<BigDecimal, BigDecimal, BigDecimal> getFunction() {
        //only the node value is used
        return (op1, ignore) -> op1.setScale(scale, RoundingMode.HALF_UP);
    }
}
