package com.github.nagyesta.yippeekijson.core.annotation;

import java.lang.annotation.*;

/**
 * Describes a parameter of a Method/Constructor to help processing externalized configuration.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface MethodParam {

    /**
     * The name of the parameter that is used in the configuration.
     *
     * @return the name used in the configuration map
     */
    String value();
}
