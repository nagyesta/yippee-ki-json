package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.annotation.MethodParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
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
    private final JsonMapper jsonMapper;

    public FunctionRegistryImpl(@NonNull final JsonMapper jsonMapper,
                                @NonNull final List<Class<? extends Supplier<?>>> autoRegisterSuppliers,
                                @NonNull final List<Class<? extends Function<?, ?>>> autoRegisterFunctions,
                                @NonNull final List<Class<? extends Predicate<Object>>> autoRegisterPredicates) {
        this.jsonMapper = jsonMapper;
        autoRegisterSuppliers.forEach(this::registerSupplierClass);
        autoRegisterFunctions.forEach(this::registerFunctionClass);
        autoRegisterPredicates.forEach(this::registerPredicateClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public <T> Supplier<T> lookupSupplier(@NonNull final Map<String, RawConfigParam> map) {
        log.debug("Starting lookup for Supplier. " + map);
        final String name = checkNameExists(map, namedSuppliers, "No Supplier found with name: ");
        return (Supplier<T>) instantiate(map, namedSuppliers.get(name));
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public <T, E> Function<T, E> lookupFunction(@NonNull final Map<String, RawConfigParam> map) {
        log.debug("Starting lookup for Function. " + map);
        final String name = checkNameExists(map, namedFunctions, "No Function found with name: ");
        return (Function<T, E>) instantiate(map, namedFunctions.get(name));
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public Predicate<Object> lookupPredicate(@NonNull final Map<String, RawConfigParam> map) {
        log.debug("Starting lookup for Predicate. " + map);
        final String name = checkNameExists(map, namedPredicates, "No Predicate found with name: ");
        return (Predicate<Object>) instantiate(map, namedPredicates.get(name));
    }

    @Override
    public JsonMapper jsonMapper() {
        return jsonMapper;
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

    private String checkNameExists(@NotNull final Map<String, RawConfigParam> map,
                                   @NotNull final Map<String, ?> constructorMap,
                                   @NotNull final String s) {
        Assert.isTrue(map.containsKey("name"), "No name found in map.");
        final RawConfigParam configParam = map.get("name");
        Assert.notNull(configParam, "No name found in map.");
        final String name = configParam.asString();
        Assert.isTrue(constructorMap.containsKey(name), s + name);
        return name;
    }

    private <T> T instantiate(@NotNull final Map<String, RawConfigParam> map,
                              @NotNull final Constructor<? extends T> constructor) {
        try {
            if (constructor.getParameters().length == 0) {
                return constructor.newInstance();
            }
            final Object[] objects = Arrays.stream(constructor.getParameters())
                    .map(p -> {
                        if (p.getType().equals(FunctionRegistry.class)) {
                            return this;
                        }
                        final MethodParam param = p.getAnnotation(MethodParam.class);
                        Assert.isTrue(map.containsKey(param.value()), "Config map has no key: " + param.value());
                        final RawConfigParam rawConfigParam = map.get(param.value());
                        return rawConfigParam.suitableFor(param.paramMap(), param.stringMap(), param.repeat());
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

        final boolean allMatch = Arrays.stream(constructor.getParameters()).allMatch(p ->
                p.getType().equals(FunctionRegistry.class)
                        || p.isAnnotationPresent(MethodParam.class));
        Assert.isTrue(allMatch, "All non-FunctionRegistry parameters must be annotated with @MethodParam on: "
                + constructor.getClass());

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
