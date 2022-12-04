package com.github.nagyesta.yippeekijson.core.config.validation;

import com.github.nagyesta.yippeekijson.core.config.validation.YippeeConfigValidator.FailureReasonCode;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates multi-attribute rules of {@link com.github.nagyesta.yippeekijson.core.config.entities.RunConfig}.
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = YippeeConfigValidator.class)
@Documented
public @interface ValidYippeeConfig {

    /**
     * Will be ignored. Validation will produce it's own messages on a field level.
     *
     * @return ignored
     * @see #messages()
     */
    String message() default "Configuration is invalid.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    MessageCode[] messages() default {
            @MessageCode(reason = FailureReasonCode.FILE_DOES_NOT_EXIST,
                    message = "The specified file does not exist."),
            @MessageCode(reason = FailureReasonCode.FILE_EXISTS,
                    message = "The specified file exists but it shouldn't."),
            @MessageCode(reason = FailureReasonCode.FILE_CANNOT_BE_READ,
                    message = "The specified file cannot be read."),
            @MessageCode(reason = FailureReasonCode.FILE_CAN_BE_READ,
                    message = "The specified file can be read but it shouldn't."),
            @MessageCode(reason = FailureReasonCode.FILE_CANNOT_BE_WRITTEN,
                    message = "The specified file cannot be written."),
            @MessageCode(reason = FailureReasonCode.FILE_CAN_BE_WRITTEN,
                    message = "The specified file can be written but it shouldn't."),
            @MessageCode(reason = FailureReasonCode.FILE_IS_NOT_A_DIRECTORY,
                    message = "The specified file is not a directory."),
            @MessageCode(reason = FailureReasonCode.FILE_IS_A_DIRECTORY,
                    message = "The specified file is a directory but it shouldn't."),
            @MessageCode(reason = FailureReasonCode.ONE_OUTPUT_MUST_BE_SET,
                    message = "Exactly one of 'output' or 'output-directory' parameters must be set."),
            @MessageCode(reason = FailureReasonCode.NULL_INCLUDE_FOUND,
                    message = "Includes cannot contain null values."),
            @MessageCode(reason = FailureReasonCode.IO_DIRECTORY_MISMATCH,
                    message = "Input file is a directory but output isn't.")
    };
}
