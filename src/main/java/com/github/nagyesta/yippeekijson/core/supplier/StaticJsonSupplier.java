package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPath;
import lombok.NonNull;

import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * {@link Supplier} returning a static JSON {@link String}.
 */
public final class StaticJsonSupplier implements Supplier<Object> {

    static final String NAME = "staticJson";

    private final String value;
    private final Object json;

    @SchemaDefinition(
            outputType = Object.class,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_SUPPLIERS, section = "Static JSON supplier"),
            sinceVersion = WikiConstants.VERSION_1_1_0,
            description = {
                    "This supplier allows us to supply more complex objects using a JSON representation in our configuration."
            },
            example = @Example(
                    in = "/examples/json/simple-accounts_in.json",
                    out = "/examples/json/add-string-json_out.json",
                    yml = "/examples/yml/add-string-json.yml",
                    note = "In this example the second supplier will return an Object containing the \"home\" key when called.")
    )
    @NamedSupplier(NAME)
    public StaticJsonSupplier(@ValueParam(docs = "The static JSON value we want to supply every time our supplier is called.")
                              @NonNull final String value,
                              @NonNull final JsonMapper jsonMapper) {
        this.value = value;
        try {
            this.json = JsonPath.parse(value, jsonMapper.parserConfiguration()).json();
        } catch (final InvalidJsonException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public Object get() {
        return json;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StaticJsonSupplier.class.getSimpleName() + "[", "]")
                .add("value='" + value + "'")
                .toString();
    }
}
