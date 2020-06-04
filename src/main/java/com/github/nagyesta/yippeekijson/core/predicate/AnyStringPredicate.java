package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import org.jetbrains.annotations.Nullable;

import java.util.StringJoiner;
import java.util.function.Predicate;

/**
 * {@link Predicate} matching any {@link String}.
 */
public final class AnyStringPredicate implements Predicate<Object> {

    static final String NAME = "anyString";

    @NamedPredicate(NAME)
    public AnyStringPredicate() {
    }

    @Override
    public boolean test(@Nullable final Object o) {
        return o == null || o instanceof String;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AnyStringPredicate.class.getSimpleName() + "[", "]")
                .toString();
    }
}
