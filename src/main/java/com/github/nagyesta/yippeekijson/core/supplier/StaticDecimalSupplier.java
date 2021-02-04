package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * {@link Supplier} returning a static {@link java.math.BigDecimal}.
 */
public final class StaticDecimalSupplier implements Supplier<BigDecimal> {

    static final String NAME = "staticDecimal";

    private final BigDecimal value;

    @SchemaDefinition(
            outputType = BigDecimal.class,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_SUPPLIERS, section = "Static BigDecimal supplier"),
            sinceVersion = WikiConstants.VERSION_1_4_0,
            description = {
                    "This supplier is essentially a fancy way of using a BigDecimal literal.",
                    "The literal value entered in the config will be converted from String."
            },
            example = @Example(
                    in = "/examples/json/simple-accounts_in.json",
                    out = "/examples/json/simple-accounts_add-decimal_out.json",
                    yml = "/examples/yml/add-decimal-json.yml",
                    note = "This example will return 4.5 when the second supplier is called.")
    )
    @NamedSupplier(NAME)
    public StaticDecimalSupplier(@ValueParam(docs = "The static value that must be returned each time the supplier is called.")
                                 @NonNull final BigDecimal value) {
        this.value = value;
    }

    @Override
    public BigDecimal get() {
        return value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StaticDecimalSupplier.class.getSimpleName() + "[", "]")
                .add("value='" + value.toPlainString() + "'")
                .toString();
    }
}
