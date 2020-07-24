package com.github.nagyesta.yippeekijson.metadata.schema.annotation;


import java.lang.annotation.*;

/**
 * Defines a property type.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface TypeDefinition {

    /**
     * The type we want to use for the property.
     *
     * @return type
     */
    Class<?> itemType() default Void.class;

    /**
     * The type parameters want to use for the {@link #itemType()}.
     *
     * @return type params
     */
    Class<?>[] itemTypeParams() default {};

    /**
     * True if this parameter expects a {@link java.util.Collection}.
     *
     * @return true/false
     */
    boolean isCollection() default false;
}
