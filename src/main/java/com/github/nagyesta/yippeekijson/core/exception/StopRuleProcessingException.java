package com.github.nagyesta.yippeekijson.core.exception;

/**
 * Exception type used to signal the need for termination in rule processing.
 */
public class StopRuleProcessingException extends RuntimeException {

    /**
     * Creates a new instance and sets the message.
     *
     * @param message the message
     */
    public StopRuleProcessingException(final String message) {
        super(message);
    }
}
