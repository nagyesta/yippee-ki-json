package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Predicate;

/**
 * {@link Predicate} matching any {@link Object} that is not null.
 */
public final class NotNullPredicate implements Predicate<Object> {

    static final String NAME = "notNull";

    @NamedPredicate(NAME)
    public NotNullPredicate() {
    }

    @Override
    public boolean test(final Object obj) {
        return Objects.nonNull(obj);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NotNullPredicate.class.getSimpleName() + "[", "]")
                .toString();
    }
}
