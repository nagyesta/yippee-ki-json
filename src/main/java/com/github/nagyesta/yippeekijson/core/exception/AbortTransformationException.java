package com.github.nagyesta.yippeekijson.core.exception;

/**
 * Used when the caller must stop processing the current file and throw away partial results.
 */
public class AbortTransformationException extends RuntimeException {

    /**
     * Creates a new instance and sets the message.
     *
     * @param message the message
     */
    public AbortTransformationException(final String message) {
        super(message);
    }
}
