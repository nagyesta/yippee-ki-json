package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonRuleRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.JsonRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

@Slf4j
public class JsonRuleRegistryImpl implements JsonRuleRegistry {

    private final Map<String, Constructor<? extends JsonRule>> namedRules = new HashMap<>();
    private final FunctionRegistry functionRegistry;

    public JsonRuleRegistryImpl(FunctionRegistry functionRegistry, List<Class<? extends JsonRule>> autoRegisterRules) {
        Assert.notNull(functionRegistry, "functionRegistry cannot be null.");
        Assert.notNull(autoRegisterRules, "autoRegisterRules cannot be null.");

        this.functionRegistry = functionRegistry;
        autoRegisterRules.forEach(this::registerRuleClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerRuleClass(Class<? extends JsonRule> rule) {
        Assert.notNull(rule, "rule cannot be null.");
        log.info("Registering rule class: " + rule.getName());
        findAnnotatedConstructor(rule).ifPresentOrElse(c -> addAnnotatedConstructor((Constructor<? extends JsonRule>) c),
                () -> {
                    throw new IllegalArgumentException("Rule is not annotated with @NamedRule: " + rule.getName());
                });
    }

    private void addAnnotatedConstructor(Constructor<? extends JsonRule> constructor) {
        Assert.notNull(constructor.getAnnotation(NamedRule.class), "Constructor in not annotated.");
        final String name = constructor.getAnnotation(NamedRule.class).value();
        Assert.isTrue(!namedRules.containsKey(name), "Duplicate named rule found: " + name);

        final Parameter[] parameters = constructor.getParameters();
        Assert.notNull(parameters, "Annotated constructor must have parameters.");
        Assert.isTrue(parameters.length == 2, "Annotated constructor must accept FunctionRegistry and RawJsonRule.");
        Assert.isTrue(FunctionRegistry.class.equals(parameters[0].getType()), "The 1st parameter must be the FunctionRegistry.");
        Assert.isTrue(RawJsonRule.class.equals(parameters[1].getType()), "The 2nd parameter must be the RawJsonRule.");

        namedRules.put(name, constructor);
    }

    private Optional<Constructor<?>> findAnnotatedConstructor(Class<? extends JsonRule> rule) {
        Assert.notNull(rule, "rule cannot be null.");
        return Arrays.stream(rule.getDeclaredConstructors())
                .filter(c -> c.isAnnotationPresent(NamedRule.class))
                .findFirst();
    }

    @Override
    public JsonRule newInstanceFrom(RawJsonRule source) throws IllegalStateException {
        Assert.notNull(source, "source cannot be null");
        Assert.notNull(source.getName(), "source.name cannot be null");
        Assert.notNull(source.getOrder(), "source.order cannot be null");

        final String name = source.getName();
        log.info("Received rule name: " + name);
        Assert.state(namedRules.containsKey(name), "No rule found for name: " + name);

        final Constructor<? extends JsonRule> constructor = namedRules.get(name);
        try {
            log.debug("Returning instance of class: " + constructor.getDeclaringClass().getName());
            return constructor.newInstance(functionRegistry, source);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
