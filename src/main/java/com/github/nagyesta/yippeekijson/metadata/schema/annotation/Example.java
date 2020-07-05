package com.github.nagyesta.yippeekijson.metadata.schema.annotation;

import java.lang.annotation.*;

/**
 * Defines links to a simple example where the component is used.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface Example {

    /**
     * The input classpath resource of the example.
     *
     * @return input
     */
    String in() default "";

    /**
     * The output classpath resource of the example.
     *
     * @return output
     */
    String out() default "";

    /**
     * The yaml config classpath resource of the example.
     *
     * @return output
     */
    String yml() default "";

    /**
     * A comment that can be used to describe what is happening in the example.
     *
     * @return comment
     */
    String[] note() default "";

    /**
     * Indicates that the example cannot be executed in tests.
     *
     * @return true if test should not be executed
     */
    boolean skipTest() default false;
}
