package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Predicate;

/**
 * {@link Predicate} matching any {@link Object} that is null.
 */
public final class IsNullPredicate implements Predicate<Object> {

    static final String NAME = "isNull";

    @NamedPredicate(NAME)
    public IsNullPredicate() {
    }

    @Override
    public boolean test(@Nullable final Object obj) {
        return Objects.isNull(obj);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IsNullPredicate.class.getSimpleName() + "[", "]")
                .toString();
    }
}
