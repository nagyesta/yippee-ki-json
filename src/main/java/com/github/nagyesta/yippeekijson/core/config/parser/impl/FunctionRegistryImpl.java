package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.annotation.MethodParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Slf4j
public class FunctionRegistryImpl implements FunctionRegistry {

    private final Map<String, Constructor<? extends Supplier<?>>> namedSuppliers = new HashMap<>();
    private final Map<String, Constructor<? extends Predicate<?>>> namedPredicates = new HashMap<>();
    private final Map<String, Constructor<? extends Function<?, ?>>> namedFunctions = new HashMap<>();

    public FunctionRegistryImpl(List<Class<? extends Supplier<?>>> autoRegisterSuppliers,
                                List<Class<? extends Function<?, ?>>> autoRegisterFunctions,
                                List<Class<? extends Predicate<?>>> autoRegisterPredicates) {
        Assert.notNull(autoRegisterSuppliers, "autoRegisterSuppliers cannot be null.");
        Assert.notNull(autoRegisterFunctions, "autoRegisterFunctions cannot be null.");
        Assert.notNull(autoRegisterPredicates, "autoRegisterPredicates cannot be null.");
        autoRegisterSuppliers.forEach(this::registerSupplierClass);
        autoRegisterFunctions.forEach(this::registerFunctionClass);
        autoRegisterPredicates.forEach(this::registerPredicateClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Supplier<T> lookupSupplier(Map<String, String> map) {
        log.debug("Starting lookup for Supplier. " + map);
        final String name = checkNameExists(map, namedSuppliers, "No Supplier found with name: ");
        return (Supplier<T>) instantiate(map, namedSuppliers.get(name));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, E> Function<T, E> lookupFunction(Map<String, String> map) {
        log.debug("Starting lookup for Function. " + map);
        final String name = checkNameExists(map, namedFunctions, "No Function found with name: ");
        return (Function<T, E>) instantiate(map, namedFunctions.get(name));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Predicate<T> lookupPredicate(Map<String, String> map) {
        log.debug("Starting lookup for Predicate. " + map);
        final String name = checkNameExists(map, namedPredicates, "No Predicate found with name: ");
        return (Predicate<T>) instantiate(map, namedPredicates.get(name));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerSupplierClass(Class<? extends Supplier<?>> supplier) {
        Assert.notNull(supplier, "Supplier cannot be null.");
        log.info("Registering Supplier class: " + supplier.getName());
        findAnnotatedConstructor(supplier, NamedSupplier.class)
                .ifPresentOrElse(c -> this.addAnnotatedConstructor(namedSuppliers, (Constructor<? extends Supplier<?>>) c,
                        NamedSupplier.class, NamedSupplier::value), () -> {
                    throw new IllegalArgumentException("Rule is not annotated with @NamedSupplier: " + supplier.getName());
                });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerFunctionClass(Class<? extends Function<?, ?>> function) {
        Assert.notNull(function, "Function cannot be null.");
        log.info("Registering Supplier class: " + function.getName());
        findAnnotatedConstructor(function, NamedFunction.class)
                .ifPresentOrElse(c -> this.addAnnotatedConstructor(namedFunctions, (Constructor<? extends Function<?, ?>>) c,
                        NamedFunction.class, NamedFunction::value), () -> {
                    throw new IllegalArgumentException("Rule is not annotated with @NamedFunction: " + function.getName());
                });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerPredicateClass(Class<? extends Predicate<?>> predicate) {
        Assert.notNull(predicate, "Predicate cannot be null.");
        log.info("Registering Predicate class: " + predicate.getName());
        findAnnotatedConstructor(predicate, NamedPredicate.class)
                .ifPresentOrElse(c -> this.addAnnotatedConstructor(namedPredicates, (Constructor<? extends Predicate<?>>) c,
                        NamedPredicate.class, NamedPredicate::value), () -> {
                    throw new IllegalArgumentException("Rule is not annotated with @NamedPredicate: " + predicate.getName());
                });
    }

    private <T> String checkNameExists(Map<String, String> map, Map<String, ?> constructorMap, String s) {
        Assert.notNull(map, "map cannot be null.");
        Assert.isTrue(map.containsKey("name"), "No name found in map.");
        final String name = map.get("name");
        Assert.isTrue(constructorMap.containsKey(name), s + name);
        return name;
    }

    private <T> T instantiate(Map<String, String> map, Constructor<? extends T> constructor) {
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
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private <F, T extends Annotation> void addAnnotatedConstructor(
            Map<String, Constructor<? extends F>> map, Constructor<? extends F> constructor, Class<T> annotation,
            Function<T, String> nameExtractorFunction) {
        Assert.notNull(constructor.getAnnotation(annotation), "Constructor in not annotated.");
        final String name = nameExtractorFunction.apply(constructor.getAnnotation(annotation));

        final boolean allMatch = Arrays.stream(constructor.getParameters()).allMatch(p -> p.isAnnotationPresent(MethodParam.class));
        Assert.isTrue(allMatch, "All parameters must be annotated with @MethodParam on: " + constructor.getClass());

        Assert.isTrue(!map.containsKey(name), "Duplicate named function found: " + name);

        map.put(name, constructor);
    }

    private <F, T extends Annotation> Optional<Constructor<?>> findAnnotatedConstructor(
            Class<? extends F> sourceClass, Class<T> annotation) {
        return Arrays.stream(sourceClass.getDeclaredConstructors())
                .filter(c -> c.isAnnotationPresent(annotation))
                .findFirst();
    }

}
