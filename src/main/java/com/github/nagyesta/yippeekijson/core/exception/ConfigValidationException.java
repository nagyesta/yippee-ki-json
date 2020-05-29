package com.github.nagyesta.yippeekijson.core.exception;

import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;

import javax.validation.ConstraintViolation;
import java.util.Collections;
import java.util.Set;

/**
 * Exception for configuration validation failures.
 */
public class ConfigValidationException extends Exception {
    private final Set<ConstraintViolation<RunConfig>> violations;

    /**
     * Default constructor.
     */
    public ConfigValidationException() {
        this(Collections.emptySet());
    }

    /**
     * Sets violations to the new instance.
     *
     * @param violations the violations causing this exception
     */
    public ConfigValidationException(final Set<ConstraintViolation<RunConfig>> violations) {
        this.violations = violations;
    }

    /**
     * The violations causing this exception.
     *
     * @return a set of violations.
     */
    public Set<ConstraintViolation<RunConfig>> getViolations() {
        return Collections.unmodifiableSet(violations);
    }
}
