package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;

import java.util.StringJoiner;
import java.util.function.Predicate;

/**
 * {@link Predicate} matching any {@link String}.
 */
public final class AnyStringPredicate implements Predicate<String> {

    @NamedPredicate("anyString")
    public AnyStringPredicate() {
    }

    @Override
    public boolean test(final String s) {
        return true;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AnyStringPredicate.class.getSimpleName() + "[", "]")
                .toString();
    }
}
