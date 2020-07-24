package com.github.nagyesta.yippeekijson.core.annotation;

import java.lang.annotation.*;

/**
 * Describes a parameter of a constructor allowing use {@link String} data.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface ValueParam {

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

    /**
     * Provides a short documentation about this parameter.
     *
     * @return Description fo the parameter.
     */
    String docs() default "";

    /**
     * Defines what the type will be for the individual {@link java.util.Collection} entries. Mandatory if used for a
     * {@link java.util.Collection} during injection.
     *
     * @return the type of a single item in the {@link java.util.Collection}.
     */
    Class<?> itemType() default Void.class;
}
