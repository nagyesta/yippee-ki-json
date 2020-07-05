package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Predicate;

/**
 * {@link Predicate} matching any {@link Object} that is null.
 */
public final class IsNullPredicate implements Predicate<Object> {

    static final String NAME = "isNull";

    @SchemaDefinition(
            inputType = Object.class,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_PREDICATES, section = "Is null predicate"),
            sinceVersion = WikiConstants.VERSION_1_1_0,
            description = {
                    "This predicate returns true for null."
            },
            example = @Example(
                    in = "/examples/json/delete-from-account_in.json",
                    out = "/examples/json/delete-from-account-isnull_out.json",
                    yml = "/examples/yml/delete-from-isnull.yml",
                    note = {
                            "In this example all of the keys were removed because none of them were null."
                    })
    )
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
