package com.github.nagyesta.yippeekijson.metadata.schema.annotation;

import java.lang.annotation.*;

/**
 * Defines a common String based type for our schema. Only intended to be used
 * in the {@link com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CommonStringTypeDefinition {

    /**
     * The name of the common type definition in our JSON schema.
     *
     * @return schema type
     */
    String typeName();

    /**
     * The regular expression all values must match.
     *
     * @return pattern
     */
    String regex() default "";

    /**
     * The enum type we want to represent with this common type.
     *
     * @return enum type
     */
    Class<? extends Enum> enumType() default Enum.class;

    /**
     * The enumerated values allowed by this type.
     *
     * @return enumerated values
     */
    String[] values() default {};

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
