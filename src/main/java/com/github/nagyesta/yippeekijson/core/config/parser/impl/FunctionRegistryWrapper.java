package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.annotation.Injectable;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Injectable(forType = FunctionRegistry.class)
public final class FunctionRegistryWrapper implements FunctionRegistry {

    private FunctionRegistry wrapped;

    @Override
    public @NotNull <T> Supplier<T> lookupSupplier(final Map<String, RawConfigParam> map) {
        return wrapped.lookupSupplier(map);
    }

    @Override
    public @NotNull <T, E> Function<T, E> lookupFunction(final Map<String, RawConfigParam> map) {
        return wrapped.lookupFunction(map);
    }

    @Override
    public @NotNull Predicate<Object> lookupPredicate(final Map<String, RawConfigParam> map) {
        return wrapped.lookupPredicate(map);
    }

    @Override
    public @NotNull Predicate<Object> lookupPredicate(final Map<String, RawConfigParam> map, final Predicate<Object> defaultValue) {
        return wrapped.lookupPredicate(map, defaultValue);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void registerSupplierClass(final Class<? extends Supplier<?>> supplier) {
        throw new UnsupportedOperationException("Configuration must be done through the wrapped instance.");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void registerFunctionClass(final Class<? extends Function<?, ?>> function) {
        throw new UnsupportedOperationException("Configuration must be done through the wrapped instance.");
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void registerPredicateClass(final Class<? extends Predicate<?>> predicate) {
        throw new UnsupportedOperationException("Configuration must be done through the wrapped instance.");
    }

    public void setWrapped(final FunctionRegistry wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void afterPropertiesSet() {
        //ignore
    }

    @Override
    public void setApplicationContext(final @NotNull ApplicationContext applicationContext) throws BeansException {
        //ignore
    }
}
