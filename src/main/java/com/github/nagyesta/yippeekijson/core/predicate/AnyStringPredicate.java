package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import org.jetbrains.annotations.Nullable;

import java.util.StringJoiner;
import java.util.function.Predicate;

/**
 * {@link Predicate} matching any {@link String}.
 */
public final class AnyStringPredicate implements Predicate<Object> {

    static final String NAME = "anyString";

    @SuppressWarnings("DefaultAnnotationParam")
    @SchemaDefinition(
            inputType = String.class,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_PREDICATES, section = "Any String predicate"),
            sinceVersion = WikiConstants.VERSION_1_0_0,
            description = {
                    "This predicate returns true for any String."
            },
            example = @Example(
                    in = "/examples/json/string-date_in.json",
                    out = "/examples/json/string-date_out.json",
                    yml = "/examples/yml/string-date.yml",
                    note = {
                            "In this example the date manipulation function was not restricted as the any String predicate",
                            "returned true for all input Strings."
                    })
    )
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
