package com.github.nagyesta.yippeekijson.core.config.validation;

import java.lang.annotation.*;

/**
 * Provides validation messages based on failure reason codes when validation fails.
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface MessageCode {

    YippeeConfigValidator.FailureReasonCode reason();

    String message();
}
