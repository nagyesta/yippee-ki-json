package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.annotation.EmbedParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import com.github.nagyesta.yippeekijson.core.predicate.helper.CombiningPredicateSupport;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

/**
 * {@link java.util.function.Predicate} evaluating a collection of embedded predicates and ensures that at least on of them is matching.
 */
public final class AnyMatchPredicate extends CombiningPredicateSupport {

    static final String NAME = "anyMatch";
    static final String PARAM_FROM = "from";

    @SchemaDefinition(
            inputType = Object.class,
            properties = @PropertyDefinitions(@PropertyDefinition(name = PARAM_FROM,
                    type = @TypeDefinition(itemType = Predicate.class, itemTypeParams = Object.class, isCollection = true),
                    docs = "The collection of Predicates we need to evaluate one by one to find the return value."
            )),
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_PREDICATES, section = "Any Match predicate"),
            sinceVersion = WikiConstants.VERSION_1_1_0,
            description = {
                    "This predicate returns true if any of the Predicates from the \"from\" collection returned true, false otherwise."
            },
            example = @Example(
                    in = "/examples/json/extended-accounts_in.json",
                    out = "/examples/json/extended-accounts_deleteFrom-keep-any_out.json",
                    yml = "/examples/yml/delete-from-any-match.yml",
                    note = {
                            "As seen in the example, this predicate matched String keys that are using either only numeric",
                            "or only lower case alpha or only upper case alpha characters so these were kept intact while",
                            "the fields with mixed names were removed."
                    })
    )
    @NamedPredicate(NAME)
    public AnyMatchPredicate(
            @EmbedParam(PARAM_FROM)
            @NotNull final Collection<Map<String, RawConfigParam>> fromPredicates,
            @NotNull final FunctionRegistry functionRegistry) {
        super(fromPredicates, functionRegistry);
    }

    @Override
    public boolean test(@Nullable final Object o) {
        return getWrappedPredicates().stream().anyMatch(p -> p.test(o));
    }

}
