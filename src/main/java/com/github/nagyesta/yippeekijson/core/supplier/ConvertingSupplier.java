package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.annotation.EmbedParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import lombok.NonNull;

import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * {@link Supplier} returning the result of a {@link Function} allied on the result of a {@link String} {@link Supplier}.
 */
public final class ConvertingSupplier implements Supplier<Object> {

    static final String NAME = "converting";

    private final Supplier<String> stringSupplier;
    private final Function<String, ?> converterFunction;

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
