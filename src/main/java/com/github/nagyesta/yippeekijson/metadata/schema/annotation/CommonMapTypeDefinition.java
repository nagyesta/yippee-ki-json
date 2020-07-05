package com.github.nagyesta.yippeekijson.metadata.schema.annotation;

import java.lang.annotation.*;

/**
 * Defines a common Map type for our schema. Only intended to be used
 * in the {@link com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CommonMapTypeDefinition {

    /**
     * The name of the common type definition in our JSON schema.
     *
     * @return schema type
     */
    String typeName();

    /**
     * The type of the allowed values in the map.
     *
     * @return value types
     */
    Class<?> valueType() default String.class;

    /**
     * The description of the property.
     *
     * @return description
     */
    String docs() default "";

    /**
     * The path to the wiki page where the component is documented.
     *
     * @return wiki link.
     */
    WikiLink wikiLink() default @WikiLink;
}
