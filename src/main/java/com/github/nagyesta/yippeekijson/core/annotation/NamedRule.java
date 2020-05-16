package com.github.nagyesta.yippeekijson.core.annotation;

import java.lang.annotation.*;

/**
 * Annotation that can assign names to {@link com.github.nagyesta.yippeekijson.core.rule.JsonRule} implementations.
 * The main benefit of having a name is being able to use shorthands in the configuration.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface NamedRule {

    /**
     * The name of the {@link com.github.nagyesta.yippeekijson.core.rule.JsonRule} implementation.
     *
     * @return name
     */
    String value();

}
