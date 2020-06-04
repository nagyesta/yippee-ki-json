package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import lombok.NonNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * {@link Predicate} combining a collection of embedded predicates.
 */
public abstract class CombiningPredicate implements Predicate<Object> {

    private final List<Predicate<Object>> wrappedPredicates;

    public CombiningPredicate(@NonNull final Collection<Map<String, RawConfigParam>> fromPredicates,
                              @NonNull final FunctionRegistry functionRegistry) {
        wrappedPredicates = fromPredicates.stream()
                .map(functionRegistry::lookupPredicate)
                .collect(Collectors.toList());
    }

    /**
     * Returns the predicates we are combining with this Predicate.
     *
     * @return the wrapped list
     */
    protected List<Predicate<Object>> getWrappedPredicates() {
        return Collections.unmodifiableList(wrappedPredicates);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("wrappedPredicates=" + wrappedPredicates)
                .toString();
    }
}
