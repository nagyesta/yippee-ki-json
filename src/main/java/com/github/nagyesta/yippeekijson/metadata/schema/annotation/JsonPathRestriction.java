package com.github.nagyesta.yippeekijson.metadata.schema.annotation;

import java.lang.annotation.*;

/**
 * Defines an implicit property for the annotated schema element.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface JsonPathRestriction {

    /**
     * The description of the property.
     *
     * @return description
     */
    String docs() default "";

    /**
     * The value the path needs to match.
     *
     * @return constant value
     */
    String constant() default "";

}
