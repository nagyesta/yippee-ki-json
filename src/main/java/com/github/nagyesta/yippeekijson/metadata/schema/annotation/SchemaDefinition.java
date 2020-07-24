package com.github.nagyesta.yippeekijson.metadata.schema.annotation;

import java.lang.annotation.*;

/**
 * The root of the schema definition metadata hierarchy.
 * Allows us to define schema restrictions and document them at the same time.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR})
public @interface SchemaDefinition {

    /**
     * The name of the type definition in our JSON schema.
     *
     * @return schema type
     */
    String typeName() default "";

    /**
     * The restriction we must apply to the path implicit property of the Json óRule.
     *
     * @return restriction
     */
    JsonPathRestriction pathRestriction() default @JsonPathRestriction;

    /**
     * The properties we know of for this type (if not auto-resolved).
     *
     * @return properties
     */
    PropertyDefinitions properties() default @PropertyDefinitions;

    /**
     * The path to the wiki page where the component is documented.
     *
     * @return wiki link.
     */
    WikiLink wikiLink() default @WikiLink;

    /**
     * The input class used by this component.
     *
     * @return input
     */
    Class<?> inputType() default Void.class;

    /**
     * The output class used by this component.
     *
     * @return output
     */
    Class<?> outputType() default Void.class;

    /**
     * The version when the component was introduced.
     *
     * @return version
     */
    String sinceVersion() default "1.0.0";

    /**
     * Paragraphs of the long description showing how the component works.
     *
     * @return description
     */
    String[] description() default {};

    /**
     * An example that can be used to illustrate usage.
     *
     * @return example
     */
    Example example() default @Example;
}
