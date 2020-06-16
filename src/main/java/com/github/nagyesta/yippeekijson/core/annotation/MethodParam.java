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

    /**
     * Set to true if the values identified by the key {@link #value()} need to be in a {@link java.util.Map}
     * format in the configuration map.
     *
     * @return true is a map of properties should be passed as value.
     */
    boolean stringMap() default false;

    /**
     * Set to true if the value identified by the key {@link #value()} need to be be considered as an item
     * in a sorted java.util.{@link java.util.Collection} of items instead of a single value.
     *
     * @return true is a collection of properties should be passed as value.
     */
    boolean repeat() default false;

    /**
     * Tells the converter that {@link com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam}
     * typed values need to be returned. This is only supported for one level of depth in the config param tree.
     * Cannot be used only together with {@link #stringMap()}, otherwise ignored.
     *
     * @return true if raw format is needed for the map.
     */
    boolean paramMap() default false;
}
