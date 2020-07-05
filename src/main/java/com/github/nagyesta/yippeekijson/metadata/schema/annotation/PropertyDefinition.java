package com.github.nagyesta.yippeekijson.metadata.schema.annotation;

import java.lang.annotation.*;

/**
 * Defines a property for the annotated schema element.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = PropertyDefinitions.class)
@Target(ElementType.ANNOTATION_TYPE)
public @interface PropertyDefinition {

    /**
     * The name of the property (using multiple tokens if we want to navigate deeper).
     * In this case we are defining objects nested into each-other until reaching the
     * leaf element defined by this particular annotation.
     *
     * @return name
     */
    String[] name();

    /**
     * The description of the property.
     *
     * @return description
     */
    String docs() default "";

    /**
     * Defines the type of this parameter.
     *
     * @return type
     */
    TypeDefinition type() default @TypeDefinition;

    /**
     * Indicates whether the property needs to be considered required.
     *
     * @return true if required
     */
    boolean required() default true;

    /**
     * The name of a common type defined in our schema. Should only contain the type name not the full reference.
     *
     * @return type name
     */
    String commonTypeRef() default "";

}
