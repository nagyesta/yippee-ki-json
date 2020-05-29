package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.MethodParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import org.springframework.util.Assert;

import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * {@link Supplier} matching a static {@link String}.
 */
public final class StaticStringSupplier implements Supplier<String> {

    private final String value;

    @NamedSupplier("staticString")
    public StaticStringSupplier(@MethodParam("value") final String value) {
        Assert.notNull(value, "value cannot be null.");
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
