package com.github.nagyesta.yippeekijson.core.exception;

import org.jetbrains.annotations.NotNull;

/**
 * Exception used for action configuration parse issues.
 */
public class ConfigParseException extends Exception {

    /**
     * Creates a new instance to be used for indicating configuration file parse issues.
     *
     * @param message the message describing what the issue was
     */
    public ConfigParseException(@NotNull final String message) {
        super(message);
    }

    /**
     * Creates a new instance to be used for indicating configuration file parse issues.
     *
     * @param message the message describing what the issue was
     * @param cause   the exception causing the failure
     */
    public ConfigParseException(@NotNull final String message, @NotNull final Throwable cause) {
        super(message, cause);
    }
}
