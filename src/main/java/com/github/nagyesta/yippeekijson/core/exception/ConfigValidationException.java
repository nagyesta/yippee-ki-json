package com.github.nagyesta.yippeekijson.core.exception;

import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import lombok.Getter;

import javax.validation.ConstraintViolation;
import java.util.Collections;
import java.util.Set;

/**
 * Exception for configuration validation failures.
 */
@Getter
public class ConfigValidationException extends Exception {
    private final Set<ConstraintViolation<RunConfig>> violations;
    private final String message;

    /**
     * Default constructor.
     *
     * @param message A brief description about the issue.
     */
    public ConfigValidationException(final String message) {
        this(message, Collections.emptySet());
    }

    /**
     * Sets violations to the new instance.
     *
     * @param message    A brief description about the issue.
     * @param violations the violations causing this exception
     */
    public ConfigValidationException(final String message, final Set<ConstraintViolation<RunConfig>> violations) {
        this.message = message;
        this.violations = violations;
    }

}
