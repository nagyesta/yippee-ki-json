package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.annotation.EmbedParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.*;
import lombok.NonNull;

import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * {@link Supplier} returning the result of a {@link Function} allied on the result of a {@link String} {@link Supplier}.
 */
public final class ConvertingSupplier implements Supplier<Object> {

    static final String PARAM_STRING_SOURCE = "stringSource";
    static final String PARAM_CONVERTER = "converter";
    static final String NAME = "converting";
    private final Supplier<String> stringSupplier;
    private final Function<String, ?> converterFunction;

    @SchemaDefinition(
            outputType = Object.class,
            properties = @PropertyDefinitions({
                    @PropertyDefinition(name = PARAM_STRING_SOURCE,
                            type = @TypeDefinition(itemType = Supplier.class, itemTypeParams = String.class),
                            docs = "The supplier of the raw String data we will use as input."),
                    @PropertyDefinition(name = PARAM_CONVERTER,
                            type = @TypeDefinition(itemType = Function.class, itemTypeParams = {String.class, Object.class}),
                            docs = "The function that will convert the string data.")
            }),
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_SUPPLIERS, section = "Converting supplier"),
            sinceVersion = WikiConstants.VERSION_1_2_0,
            description = {
                    "This supplier allows us to supply more complex objects with the help of a function that can covert",
                    "strings into other types. As long as we have the right functions this can be a very powerful supplier."
            },
            example = @Example(
                    in = "/examples/json/simple-accounts_in.json",
                    out = "/examples/json/add-string-capitalized.json",
                    yml = "/examples/yml/add-string-capitalized.yml",
                    note = "In this example the capitalized value of \"Missing\" was added thanks to the converting supplier.")
    )
    @NamedSupplier(NAME)
    public ConvertingSupplier(@EmbedParam @NonNull final Map<String, RawConfigParam> stringSource,
                              @EmbedParam @NonNull final Map<String, RawConfigParam> converter,
                              @NonNull final FunctionRegistry functionRegistry) {
        this.stringSupplier = functionRegistry.lookupSupplier(stringSource);
        this.converterFunction = functionRegistry.lookupFunction(converter);
    }

    @Override
    public Object get() {
        return converterFunction.apply(stringSupplier.get());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ConvertingSupplier.class.getSimpleName() + "[", "]")
                .add("stringSupplier=" + stringSupplier)
                .add("converterFunction=" + converterFunction)
                .toString();
    }
}
