package com.github.nagyesta.yippeekijson.core.annotation;

import java.lang.annotation.*;

/**
 * Describes a parameter of a constructor allowing use structured data with the
 * intent of using this param for a FunctionRegistry lookup.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface EmbedParam {

    /**
     * Allows explicit definition of the names we need to lookup.
     * If absent, the evaluation will fall back to:
     * <ol>
     *     <li>{@link org.springframework.beans.factory.annotation.Qualifier#value()}</li>
     *     <li>{@link javax.inject.Named#value()}</li>
     *     <li>{@link java.lang.reflect.Parameter#getName()}</li>
     * </ol>
     *
     * @return the preferred name of the parameter.
     */
    String value() default "";

    /**
     * Allows us to identify if null values are accepted by the parameter.
     * The evaluation will also respect the presence of the following
     * annotations if false.
     * <ul>
     *     <li>{@link javax.annotation.Nullable}</li>
     *     <li>{@link org.springframework.lang.Nullable}</li>
     * </ul>
     *
     * @return true if the value can be null despite not being annotates as such.
     */
    boolean nullable() default false;
}
