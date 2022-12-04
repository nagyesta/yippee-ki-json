package com.github.nagyesta.yippeekijson.core.config.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = JsonPathValidator.class)
@Documented
public @interface JsonPath {

    String message() default "Not a valid JSON Path.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
