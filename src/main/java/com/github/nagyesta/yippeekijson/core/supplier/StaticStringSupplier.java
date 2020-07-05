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
 * {@link Supplier} returning a static {@link String}.
 */
public final class StaticStringSupplier implements Supplier<String> {

    static final String NAME = "staticString";

    private final String value;

    @SuppressWarnings("DefaultAnnotationParam")
    @SchemaDefinition(
            outputType = String.class,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_SUPPLIERS, section = "Static String supplier"),
            sinceVersion = WikiConstants.VERSION_1_0_0,
            description = {
                    "This supplier is essentially a fancy way of using a String literal.",
                    "Whatever value you add to the config, it will be returned as is."
            },
            example = @Example(
                    in = "/examples/json/simple-accounts_in.json",
                    out = "/examples/json/add-string-json_out.json",
                    yml = "/examples/yml/add-string-json.yml",
                    note = "This example will return \"address\" whenever the first supplier is called.")
    )
    @NamedSupplier(NAME)
    public StaticStringSupplier(@ValueParam(docs = "The static value that must be returned each time the supplier is called.")
                                @NonNull final String value) {
        this.value = value;
    }

    @Override
    public String get() {
        return value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StaticStringSupplier.class.getSimpleName() + "[", "]")
                .add("value='" + value + "'")
                .toString();
    }
}
