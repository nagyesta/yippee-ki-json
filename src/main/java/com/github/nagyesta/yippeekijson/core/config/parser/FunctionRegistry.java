package com.github.nagyesta.yippeekijson.core.config.parser;

import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Registry for named {@link Function}, {@link Supplier}, {@link Predicate} implementations to be used during the parsing.
 */
public interface FunctionRegistry extends InitializingBean, ApplicationContextAware {

    /**
     * Finds a {@link Supplier} based on the provided input parameters.
     *
     * @param map The input parameters.
     * @param <T> The type of items we need to supply.
     * @return The configured {@link Supplier}
     */
    @NotNull <T> Supplier<T> lookupSupplier(Map<String, RawConfigParam> map);

    /**
     * Finds a {@link Function} based on the provided input parameters.
     *
     * @param map The input parameters.
     * @param <T> The source type we want to convert.
     * @param <E> The destination type we want to convert to.
     * @return The configured {@link Function}
     */
    @NotNull <T, E> Function<T, E> lookupFunction(Map<String, RawConfigParam> map);

    /**
     * Finds a {@link Predicate} based on the provided input parameters.
     *
     * @param map The input parameters.
     * @return The configured {@link Predicate}
     */
    @NotNull Predicate<Object> lookupPredicate(Map<String, RawConfigParam> map);

    /**
     * Finds a {@link Predicate} based on the provided input parameters or returns the provided default value if the map is empty.
     *
     * @param map          The input parameters.
     * @param defaultValue The default value in case the map was empty.
     * @return The configured {@link Predicate}
     */
    @NotNull
    default Predicate<Object> lookupPredicate(final Map<String, RawConfigParam> map, final Predicate<Object> defaultValue) {
        if (CollectionUtils.isEmpty(map)) {
            Assert.notNull(defaultValue, "Default value cannot be null.");
            return defaultValue;
        }
        return lookupPredicate(map);
    }

    /**
     * Registers a {@link Supplier} implementation annotated with @{@link com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier}.
     *
     * @param supplier The class being registered.
     */
    void registerSupplierClass(@NonNull Class<? extends Supplier<?>> supplier);

    /**
     * Registers a {@link Function} implementation annotated with @{@link com.github.nagyesta.yippeekijson.core.annotation.NamedFunction}.
     *
     * @param function The class being registered.
     */
    void registerFunctionClass(@NonNull Class<? extends Function<?, ?>> function);

    /**
     * Registers a {@link Predicate} implementation annotated with @{@link com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate}.
     *
     * @param predicate The class being registered.
     */
    void registerPredicateClass(@NonNull Class<? extends Predicate<?>> predicate);
}
