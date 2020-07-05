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
 * {@link java.util.function.Predicate} evaluating a collection of embedded predicates and ensures that none of them are matching.
 */
public final class NoneMatchPredicate extends CombiningPredicateSupport {

    static final String NAME = "noneMatch";
    static final String PARAM_FROM = "from";

    @SchemaDefinition(
            inputType = Object.class,
            properties = @PropertyDefinitions(@PropertyDefinition(name = PARAM_FROM,
                    type = @TypeDefinition(itemType = Predicate.class, itemTypeParams = Object.class, isCollection = true),
                    docs = "The collection of Predicates we need to evaluate one by one to find the return value."
            )),
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_PREDICATES, section = "None Match predicate"),
            sinceVersion = WikiConstants.VERSION_1_1_0,
            description = {
                    "This predicate returns true if none of the Predicates from the \"from\" collection returned true, false otherwise."
            },
            example = @Example(
                    in = "/examples/json/extended-accounts_in.json",
                    out = "/examples/json/extended-accounts_deleteFrom-keep-none_out.json",
                    yml = "/examples/yml/delete-from-none-match.yml",
                    note = {
                            "As seen in the example, this predicate matched if the key was neither only lower case alpha",
                            "nor only upper case alpha nor only numeric. Therefore only \"fullName\" was kept while every",
                            "other key was removed."
                    })
    )
    @NamedPredicate(NAME)
    public NoneMatchPredicate(@EmbedParam(PARAM_FROM)
                              @NotNull final Collection<Map<String, RawConfigParam>> fromPredicates,
                              @NotNull final FunctionRegistry functionRegistry) {
        super(fromPredicates, functionRegistry);
    }

    @Override
    public boolean test(@Nullable final Object o) {
        return getWrappedPredicates().stream().noneMatch(p -> p.test(o));
    }

}
