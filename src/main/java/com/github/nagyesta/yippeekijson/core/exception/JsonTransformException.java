package com.github.nagyesta.yippeekijson.core.exception;

/**
 * Exception signaling JSON transformation problems.
 */
public class JsonTransformException extends Exception {
    public JsonTransformException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
