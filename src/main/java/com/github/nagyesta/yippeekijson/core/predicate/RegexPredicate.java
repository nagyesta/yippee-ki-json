package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * {@link Predicate} using regular expressions to find matching {@link String} values.
 */
public final class RegexPredicate implements Predicate<Object> {

    static final String NAME = "regex";

    private final Pattern pattern;

    @NamedPredicate(NAME)
    public RegexPredicate(@ValueParam @NonNull final String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public boolean test(@Nullable final Object o) {
        return (o instanceof String) && pattern.matcher((String) o).matches();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RegexPredicate.class.getSimpleName() + "[", "]")
                .add("pattern=" + pattern)
                .toString();
    }
}
