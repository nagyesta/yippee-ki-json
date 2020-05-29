package com.github.nagyesta.yippeekijson.core.exception;

/**
 * Exception used for action configuration parse issues.
 */
public class ConfigParseException extends Exception {
    public ConfigParseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
