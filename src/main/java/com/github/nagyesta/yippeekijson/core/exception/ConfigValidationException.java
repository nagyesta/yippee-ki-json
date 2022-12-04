package com.github.nagyesta.yippeekijson.core.exception;

import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

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
    public ConfigValidationException(@NotNull final String message) {
        this(message, Collections.emptySet());
    }

    /**
     * Sets violations to the new instance.
     *
     * @param message    A brief description about the issue.
     * @param violations the violations causing this exception
     */
    public ConfigValidationException(@NotNull final String message, @NotNull final Set<ConstraintViolation<RunConfig>> violations) {
        this.message = message;
        this.violations = violations;
    }

}
