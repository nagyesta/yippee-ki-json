package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.NamedComponentUtil;
import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonRuleRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.JsonRule;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonRuleRegistryImpl extends InjectableBeanSupport implements JsonRuleRegistry {

    private final Map<String, Constructor<? extends JsonRule>> namedRules = new HashMap<>();
    private final List<Class<? extends JsonRule>> rules;

    public JsonRuleRegistryImpl(@NonNull final List<Class<? extends JsonRule>> rules) {
        super(log);
        this.rules = rules;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerRuleClass(@NonNull final Class<? extends JsonRule> rule) {
        log.info("Registering rule class: " + rule.getName());
        NamedComponentUtil.findAnnotatedConstructorOfNamedComponent(rule, NamedRule.class)
                .ifPresentOrElse(c -> addAnnotatedConstructor((Constructor<? extends JsonRule>) c),
                        () -> {
                            throw new IllegalArgumentException("Rule is not annotated with @NamedRule: " + rule.getName());
                        });
    }

    @Override
    public JsonRule newInstanceFrom(@NonNull final RawJsonRule source) throws IllegalStateException {
        Assert.notNull(source.getName(), "source.name cannot be null");
        Assert.notNull(source.getOrder(), "source.order cannot be null");

        final String name = source.getName();
        log.info("Received rule name: " + name);
        Assert.state(namedRules.containsKey(name), "No rule found for name: " + name);

        final Constructor<? extends JsonRule> constructor = namedRules.get(name);
        try {
            log.debug("Returning instance of class: " + constructor.getDeclaringClass().getName());
            final Object[] objects = prepareParams(source, constructor);
            return constructor.newInstance(objects);
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @NotNull
    private Object[] prepareParams(@NotNull final RawJsonRule rawJsonRule,
                                   @NotNull final Constructor<? extends JsonRule> constructor) {
        return Arrays.stream(constructor.getParameters()).map(p -> {
            if (p.getType().equals(RawJsonRule.class)) {
                return rawJsonRule;
            } else {
                return this.validCandidateOf(constructor, p);
            }
        }).toArray();
    }

    private void addAnnotatedConstructor(@NotNull final Constructor<? extends JsonRule> constructor) {
        Assert.notNull(constructor.getAnnotation(NamedRule.class), "Constructor in not annotated.");
        final String name = constructor.getAnnotation(NamedRule.class).value();
        Assert.isTrue(!namedRules.containsKey(name), "Duplicate named rule found: " + name);

        final Parameter[] parameters = constructor.getParameters();
        Assert.notNull(parameters, "Annotated constructor must have parameters.");
        assertAcceptsRawJsonRule(constructor);
        assertUsesValidAnnotatedTypes(constructor);
        namedRules.put(name, constructor);
    }

    private void assertUsesValidAnnotatedTypes(@NotNull final Constructor<? extends JsonRule> constructor) {
        final boolean allMatch = Arrays.stream(constructor.getParameters()).allMatch(p ->
                this.hasCandidateFor(p.getType()) || p.getType().equals(RawJsonRule.class));
        Assert.isTrue(allMatch, "Only RawJsonRule parameter can be of non-@injectable type: "
                + constructor.getDeclaringClass().getName());
    }

    private void assertAcceptsRawJsonRule(@NotNull final Constructor<? extends JsonRule> constructor) {
        final long rawRuleCount = Arrays.stream(constructor.getParameters())
                .filter(p -> p.getType().equals(RawJsonRule.class)).count();
        Assert.isTrue(rawRuleCount == 1, "Annotated constructor must accept RawJsonRule.");
    }

    @Override
    protected void afterInitialized() {
        rules.forEach(this::registerRuleClass);
    }
}
