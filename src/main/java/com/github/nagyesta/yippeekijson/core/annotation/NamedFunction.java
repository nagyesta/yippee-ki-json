package com.github.nagyesta.yippeekijson.core.annotation;

import java.lang.annotation.*;

/**
 * Annotation that can assign names to {@link java.util.function.Function} implementations.
 * The main benefit of having a name is being able to use shorthands in the configuration.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface NamedFunction {

    /**
     * The name of the {@link java.util.function.Function} implementation.
     *
     * @return name
     */
    String value();

}
