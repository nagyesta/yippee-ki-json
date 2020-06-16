package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.annotation.MethodParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPath;
import lombok.NonNull;

import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * {@link Supplier} matching a static JSON {@link String}.
 */
public final class StaticJsonSupplier implements Supplier<Object> {

    static final String NAME = "staticJson";
    static final String PARAM_VALUE = "value";

    private final String value;
    private final Object json;

    @NamedSupplier(NAME)
    public StaticJsonSupplier(@MethodParam(PARAM_VALUE) @NonNull final String value,
                              @NonNull final FunctionRegistry functionRegistry) {
        this.value = value;
        try {
            this.json = JsonPath.parse(value, functionRegistry.jsonMapper().parserConfiguration()).json();
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
