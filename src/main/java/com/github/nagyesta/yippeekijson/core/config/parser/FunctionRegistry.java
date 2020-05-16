package com.github.nagyesta.yippeekijson.core.config.parser;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Registry for named {@link Function}, {@link Supplier}, {@link Predicate} implementations to be used during the parsing.
 */
public interface FunctionRegistry {

    /**
     * Finds a {@link Supplier} based on the provided input parameters.
     *
     * @param map The input parameters.
     * @param <T> The type of items we need to supply.
     * @return The configured {@link Supplier}
     */
    <T> Supplier<T> lookupSupplier(Map<String, String> map);

    /**
     * Finds a {@link Function} based on the provided input parameters.
     *
     * @param map The input parameters.
     * @param <T> The source type we want to convert.
     * @param <E> The destination type we want to convert to.
     * @return The configured {@link Function}
     */
    <T, E> Function<T, E> lookupFunction(Map<String, String> map);

    /**
     * Finds a {@link Predicate} based on the provided input parameters.
     *
     * @param map The input parameters.
     * @param <T> The type of items we need to test.
     * @return The configured {@link Predicate}
     */
    <T> Predicate<T> lookupPredicate(Map<String, String> map);

    /**
     * Registers a {@link Supplier} implementation annotated with @{@link com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier}.
     *
     * @param supplier The class being registered.
     */
    void registerSupplierClass(Class<? extends Supplier<?>> supplier);

    /**
     * Registers a {@link Function} implementation annotated with @{@link com.github.nagyesta.yippeekijson.core.annotation.NamedFunction}.
     *
     * @param function The class being registered.
     */
    void registerFunctionClass(Class<? extends Function<?, ?>> function);

    /**
     * Registers a {@link Predicate} implementation annotated with @{@link com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate}.
     *
     * @param predicate The class being registered.
     */
    void registerPredicateClass(Class<? extends Predicate<?>> predicate);
}
