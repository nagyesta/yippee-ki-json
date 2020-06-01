package com.github.nagyesta.yippeekijson.core.exception;

import org.jetbrains.annotations.NotNull;

/**
 * Exception signaling JSON transformation problems.
 */
public class JsonTransformException extends Exception {
    /**
     * Created a new instance that can be used to signal JSON transformation problems.
     *
     * @param message a brief description of the issue
     * @param cause   the original exception causing the failure
     */
    public JsonTransformException(@NotNull final String message, @NotNull final Throwable cause) {
        super(message, cause);
    }
}
