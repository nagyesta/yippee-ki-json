package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import lombok.NonNull;

import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * {@link Supplier} returning a static {@link Boolean}.
 */
public final class StaticBooleanSupplier implements Supplier<Boolean> {

    static final String NAME = "staticBoolean";

    private final Boolean value;

    @SchemaDefinition(
            outputType = Boolean.class,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_SUPPLIERS, section = "Static Boolean supplier"),
            sinceVersion = WikiConstants.VERSION_1_4_0,
            description = {
                    "This supplier is essentially a fancy way of using a Boolean literal.",
                    "The literal value entered in the config will be converted from String."
            },
            example = @Example(
                    in = "/examples/json/simple-accounts_in.json",
                    out = "/examples/json/simple-accounts_add-boolean_out.json",
                    yml = "/examples/yml/add-boolean-json.yml",
                    note = "This example will return false when the second supplier is called.")
    )
    @NamedSupplier(NAME)
    public StaticBooleanSupplier(@ValueParam(docs = "The static value that must be returned each time the supplier is called.")
                                 @NonNull final Boolean value) {
        this.value = value;
    }

    @Override
    public Boolean get() {
        return value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StaticBooleanSupplier.class.getSimpleName() + "[", "]")
                .add("value='" + value + "'")
                .toString();
    }
}
