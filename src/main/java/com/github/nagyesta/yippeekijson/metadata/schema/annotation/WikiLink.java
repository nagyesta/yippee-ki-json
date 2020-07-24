package com.github.nagyesta.yippeekijson.metadata.schema.annotation;

import java.lang.annotation.*;

/**
 * Defines a link pointing to a wiki page we will use to describe this component.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface WikiLink {

    /**
     * The file name of the wiki page including the file extension.
     *
     * @return file name
     */
    String file() default "";

    /**
     * The section we want to refer to within the wiki page.
     *
     * @return section name
     */
    String section() default "";

    /**
     * The full URI we want to use. Will override the calculated values of {@link #file()} and {@link #section()}.
     *
     * @return the URI of the wiki page we want to reference
     */
    String uri() default "";
}
