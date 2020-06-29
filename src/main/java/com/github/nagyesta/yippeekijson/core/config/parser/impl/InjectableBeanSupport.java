package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.annotation.Injectable;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import javax.inject.Named;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class InjectableBeanSupport implements ApplicationContextAware, InitializingBean {

    private final Map<Class<?>, Map<String, Object>> injectableBeans = new HashMap<>();
    private final Logger log;
    private ApplicationContext applicationContext;

    protected InjectableBeanSupport(final Logger log) {
        this.log = log;
    }

    /**
     * Returns a valid candidate for injection.
     *
     * @param constructor The constructor we need to call
     * @param parameter   The parameter we want to inject to
     * @return An injectable bean if the parameter type allows
     */
    protected Object validCandidateOf(@NotNull final Constructor<?> constructor, @NotNull final Parameter parameter) {
        final String parameterName = parameter.getName();
        Assert.isTrue(this.injectableBeans.containsKey(parameter.getType()),
                "Parameter " + constructor.getDeclaringClass().getSimpleName() + "."
                        + parameterName + " is neither annotated with a param annotation nor is having an @Injectable type.");
        Map<String, Object> candidates = injectableBeans.get(parameter.getType());
        return primaryCandidate(candidates, constructor, parameter);
    }

    /**
     * Returns true if the type provided has at least one registered bean candidate.
     *
     * @param type The parameter type we need to inject to
     * @return true is suitable candidate exists
     */
    protected boolean hasCandidateFor(@NotNull final Class<?> type) {
        return this.injectableBeans.containsKey(type);
    }

    private Object primaryCandidate(@NotNull final Map<String, Object> candidates,
                                    @NotNull final Constructor<?> constructor,
                                    @NotNull final Parameter parameter) {
        if (candidates.size() == 1) {
            return singleCandidate(candidates);
        } else {
            return candidateByName(candidates, constructor, parameter);
        }
    }

    private Object candidateByName(@NotNull final Map<String, Object> candidates,
                                   @NotNull final Constructor<?> constructor,
                                   @NotNull final Parameter parameter) {
        String name = parameter.getName();
        if (parameter.isAnnotationPresent(Qualifier.class)) {
            name = Objects.requireNonNullElse(StringUtils.trimToNull(parameter.getAnnotation(Qualifier.class).value()), name);
        } else if (parameter.isAnnotationPresent(Named.class)) {
            name = Objects.requireNonNullElse(StringUtils.trimToNull(parameter.getAnnotation(Named.class).value()), name);
        }
        final String parameterReference = constructor.getDeclaringClass().getSimpleName() + "."
                + name;
        Assert.isTrue(candidates.containsKey(name),
                "Parameter " + parameterReference + " defined an unknown bean name: " + name + ".\n"
                        + "Known names are: " + candidates.keySet());
        return candidates.get(name);
    }

    @NotNull
    private Object singleCandidate(@NotNull final Map<String, Object> candidates) {
        return candidates.values().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Single entity is not found."));
    }

    /**
     * Will be called at the very last line of {@link #afterPropertiesSet()}.
     */
    protected abstract void afterInitialized();

    @Override
    public void setApplicationContext(final @NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Registering injectable beans.");
        this.applicationContext.getBeansWithAnnotation(Injectable.class)
                .forEach((name, bean) -> {
                    Class<?> beanClass = ClassUtils.getUserClass(bean);
                    log.debug("Processing: " + beanClass.getName());
                    Assert.isTrue(beanClass.isAnnotationPresent(Injectable.class),
                            "Bean class is supposed to be annotated: " + beanClass.getName());
                    Class<?> forType = beanClass.getAnnotation(Injectable.class).forType();
                    final Map<String, Object> map = this.injectableBeans.getOrDefault(forType, new HashMap<>());
                    log.debug("Put " + name + " - " + forType.getName());
                    map.put(name, bean);
                    this.injectableBeans.put(forType, map);
                });
        this.afterInitialized();
    }
}
