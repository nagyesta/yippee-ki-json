package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import lombok.NonNull;

import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * {@link Supplier} returning a static {@link String}.
 */
public final class StaticStringSupplier implements Supplier<String> {

    static final String NAME = "staticString";

    private final String value;

    @NamedSupplier(NAME)
    public StaticStringSupplier(@ValueParam @NonNull final String value) {
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
