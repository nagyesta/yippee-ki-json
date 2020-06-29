package com.github.nagyesta.yippeekijson.core.annotation;

import java.lang.annotation.*;

/**
 * Marks a class used for a Spring bean to allow injection of the bean in question into
 * rules / functions / suppliers / predicates.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Injectable {

    /**
     * Defines which is the type we can look for when we are instantiating the objects.
     *
     * @return the type we want to use.
     */
    Class<?> forType();
}
