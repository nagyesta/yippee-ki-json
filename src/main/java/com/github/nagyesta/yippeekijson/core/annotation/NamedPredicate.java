package com.github.nagyesta.yippeekijson.core.annotation;

import java.lang.annotation.*;

/**
 * Annotation that can assign names to {@link java.util.function.Predicate} implementations.
 * The main benefit of having a name is being able to use shorthands in the configuration.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface NamedPredicate {

    /**
     * The name of the {@link java.util.function.Predicate} implementation.
     *
     * @return name
     */
    String value();

}
