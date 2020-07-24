package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Predicate;

/**
 * {@link Predicate} matching any {@link Object} that is not null.
 */
public final class NotNullPredicate implements Predicate<Object> {

    static final String NAME = "notNull";

    @SchemaDefinition(
            inputType = Object.class,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_PREDICATES, section = "Not null predicate"),
            sinceVersion = WikiConstants.VERSION_1_1_0,
            description = {
                    "This predicate returns true for anything that is not null."
            },
            example = @Example(
                    in = "/examples/json/delete-from-account_in.json",
                    out = "/examples/json/delete-from-account_in.json",
                    yml = "/examples/yml/delete-from-nonnull.yml",
                    note = {
                            "In this example all of the keys were kept because none of them were null."
                    })
    )
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
