package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
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

    @SchemaDefinition(
            inputType = String.class,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_PREDICATES, section = "RegEx predicate"),
            sinceVersion = WikiConstants.VERSION_1_1_0,
            description = {
                    "This predicate returns true if input value matches the regular expression defined in the \"pattern\" parameter."
            },
            example = @Example(
                    in = "/examples/json/delete-from-account_in.json",
                    out = "/examples/json/delete-from-account_out.json",
                    yml = "/examples/yml/delete-from.yml",
                    note = {
                            "This example shown 2 RegEx predicates right away. One told the rule to keep lower case alpha keys,",
                            "while the other selected the password field for removal."
                    })
    )
    @NamedPredicate(NAME)
    public RegexPredicate(@ValueParam(docs = "The regular expression we want to match.")
                          @NonNull final String pattern) {
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
