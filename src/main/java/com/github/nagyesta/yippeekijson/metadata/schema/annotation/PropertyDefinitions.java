package com.github.nagyesta.yippeekijson.metadata.schema.annotation;

import java.lang.annotation.*;

/**
 * Defines restrictions for the annotated type, allowing definition of the known properties
 * and the number of properties required overall.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface PropertyDefinitions {

    /**
     * An array of property definitions, listing the known properties (in case auto-resolution is not possible or not desired).
     *
     * @return properties
     */
    PropertyDefinition[] value() default {};

    /**
     * The minimum number of properties required for this type.
     *
     * @return min number of properties needed
     */
    int minProperties() default -1;

    /**
     * The maximum number of properties required for this type.
     *
     * @return max number of properties needed
     */
    int maxProperties() default -1;
}
