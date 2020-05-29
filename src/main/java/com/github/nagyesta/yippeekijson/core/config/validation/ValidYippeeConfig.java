package com.github.nagyesta.yippeekijson.core.config.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Validates multi-attribute rules of {@link com.github.nagyesta.yippeekijson.core.config.entities.RunConfig}.
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = YippeeConfigValidator.class)
@Documented
public @interface ValidYippeeConfig {

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
