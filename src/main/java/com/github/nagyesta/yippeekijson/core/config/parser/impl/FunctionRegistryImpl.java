package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.annotation.MethodParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Slf4j
public class FunctionRegistryImpl implements FunctionRegistry {

    private final @NotNull Map<String, Constructor<?>> namedSuppliers = new HashMap<>();
    private final @NotNull Map<String, Constructor<?>> namedPredicates = new HashMap<>();
    private final @NotNull Map<String, Constructor<?>> namedFunctions = new HashMap<>();

    public FunctionRegistryImpl(@NonNull final List<Class<? extends Supplier<?>>> autoRegisterSuppliers,
                                @NonNull final List<Class<? extends Function<?, ?>>> autoRegisterFunctions,
                                @NonNull final List<Class<? extends Predicate<?>>> autoRegisterPredicates) {
        autoRegisterSuppliers.forEach(this::registerSupplierClass);
        autoRegisterFunctions.forEach(this::registerFunctionClass);
        autoRegisterPredicates.forEach(this::registerPredicateClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public <T> Supplier<T> lookupSupplier(@NonNull final Map<String, String> map) {
        log.debug("Starting lookup for Supplier. " + map);
        final String name = checkNameExists(map, namedSuppliers, "No Supplier found with name: ");
        return (Supplier<T>) instantiate(map, namedSuppliers.get(name));
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public <T, E> Function<T, E> lookupFunction(@NonNull final Map<String, String> map) {
        log.debug("Starting lookup for Function. " + map);
        final String name = checkNameExists(map, namedFunctions, "No Function found with name: ");
        return (Function<T, E>) instantiate(map, namedFunctions.get(name));
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public <T> Predicate<T> lookupPredicate(@NonNull final Map<String, String> map) {
        log.debug("Starting lookup for Predicate. " + map);
        final String name = checkNameExists(map, namedPredicates, "No Predicate found with name: ");
        return (Predicate<T>) instantiate(map, namedPredicates.get(name));
    }

    @Override
    public void registerSupplierClass(@NonNull final Class<? extends Supplier<?>> supplier) {
        findAnnotatedConstructor(supplier, NamedSupplier.class, NamedSupplier::value, namedSuppliers);

    }

    @Override
    public void registerFunctionClass(@NonNull final Class<? extends Function<?, ?>> function) {
        findAnnotatedConstructor(function, NamedFunction.class, NamedFunction::value, namedFunctions);
    }

    @Override
    public void registerPredicateClass(@NonNull final Class<? extends Predicate<?>> predicate) {
        findAnnotatedConstructor(predicate, NamedPredicate.class, NamedPredicate::value, namedPredicates);
    }

    private <T> String checkNameExists(@NotNull final Map<String, String> map,
                                       @NotNull final Map<String, ?> constructorMap,
                                       @NotNull final String s) {
        Assert.isTrue(map.containsKey("name"), "No name found in map.");
        final String name = map.get("name");
        Assert.isTrue(constructorMap.containsKey(name), s + name);
        return name;
    }

    private <T> T instantiate(@NotNull final Map<String, String> map,
                              @NotNull final Constructor<? extends T> constructor) {
        try {
            if (constructor.getParameters().length == 0) {
                return constructor.newInstance();
            }

            final Object[] objects = Arrays.stream(constructor.getParameters())
                    .map(p -> p.getAnnotation(MethodParam.class).value())
                    .map(key -> {
                        Assert.isTrue(map.containsKey(key), "Config map has no key: " + key);
                        return map.get(key);
                    })
                    .toArray();
            return constructor.newInstance(objects);
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private <T extends Annotation> void addAnnotatedConstructor(
            @NotNull final Map<String, Constructor<?>> map,
            @NotNull final Constructor<?> constructor,
            @NotNull final Class<T> annotation,
            @NotNull final Function<T, String> nameExtractorFunction) {
        Assert.notNull(constructor.getAnnotation(annotation), "Constructor in not annotated.");
        final String name = nameExtractorFunction.apply(constructor.getAnnotation(annotation));

        final boolean allMatch = Arrays.stream(constructor.getParameters()).allMatch(p -> p.isAnnotationPresent(MethodParam.class));
        Assert.isTrue(allMatch, "All parameters must be annotated with @MethodParam on: " + constructor.getClass());

        Assert.isTrue(!map.containsKey(name), "Duplicate named function found: " + name);

        map.put(name, constructor);
    }

    private <F, T extends Annotation> void findAnnotatedConstructor(
            @NotNull final Class<? extends F> sourceClass,
            @NotNull final Class<T> annotation,
            @NotNull final Function<T, String> nameExtractorFunction,
            @NotNull final Map<String, Constructor<?>> map) {
        log.info("Registering @" + annotation.getSimpleName() + " annotated class: " + sourceClass.getName());
        Arrays.stream(sourceClass.getDeclaredConstructors())
                .filter(c -> c.isAnnotationPresent(annotation))
                .findFirst()
                .ifPresentOrElse(c -> this.addAnnotatedConstructor(map, c,
                        annotation, nameExtractorFunction), () -> {
                    throw new IllegalArgumentException("Rule is not annotated with @"
                            + annotation.getSimpleName() + ": " + sourceClass.getName());
                });
    }

}
