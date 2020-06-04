package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.annotation.MethodParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * {@link java.util.function.Predicate} evaluating a collection of embedded predicates and ensures that all of them are matching.
 */
public final class AllMatchPredicate extends CombiningPredicate {

    static final String NAME = "allMatch";
    static final String PARAM_FROM = "from";

    @NamedPredicate(NAME)
    public AllMatchPredicate(@MethodParam(value = PARAM_FROM, stringMap = true, repeat = true, paramMap = true)
                             @NotNull final Collection<Map<String, RawConfigParam>> fromPredicates,
                             @NotNull final FunctionRegistry functionRegistry) {
        super(fromPredicates, functionRegistry);
    }

    @Override
    public boolean test(@Nullable final Object o) {
        return getWrappedPredicates().stream().allMatch(p -> p.test(o));
    }

}
