package com.github.nagyesta.yippeekijson.core.config.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


/**
 * Validates files used in {@link com.github.nagyesta.yippeekijson.core.config.entities.RunConfig}.
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = FileConstraintValidator.class)
@Documented
public @interface ValidFile {

    /**
     * Will be ignored. Validation will produce it's own messages on a field level.
     *
     * @return ignored
     * @see #messages()
     */
    String message() default "Not a valid File.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    MessageCode[] messages() default {
            @MessageCode(reason = YippeeConfigValidator.FailureReasonCode.FILE_DOES_NOT_EXIST,
                    message = "The specified file does not exist."),
            @MessageCode(reason = YippeeConfigValidator.FailureReasonCode.FILE_EXISTS,
                    message = "The specified file exists but it shouldn't."),
            @MessageCode(reason = YippeeConfigValidator.FailureReasonCode.FILE_CANNOT_BE_READ,
                    message = "The specified file cannot be read."),
            @MessageCode(reason = YippeeConfigValidator.FailureReasonCode.FILE_CAN_BE_READ,
                    message = "The specified file can be read but it shouldn't."),
            @MessageCode(reason = YippeeConfigValidator.FailureReasonCode.FILE_CANNOT_BE_WRITTEN,
                    message = "The specified file cannot be written."),
            @MessageCode(reason = YippeeConfigValidator.FailureReasonCode.FILE_CAN_BE_WRITTEN,
                    message = "The specified file can be written but it shouldn't."),
            @MessageCode(reason = YippeeConfigValidator.FailureReasonCode.FILE_IS_NOT_A_DIRECTORY,
                    message = "The specified file is not a directory."),
            @MessageCode(reason = YippeeConfigValidator.FailureReasonCode.FILE_IS_A_DIRECTORY,
                    message = "The specified file is a directory but it shouldn't.")
    };

    FileCheck exists() default FileCheck.ANY;

    FileCheck canRead() default FileCheck.ANY;

    FileCheck canWrite() default FileCheck.ANY;

    FileCheck isDirectory() default FileCheck.ANY;

    enum FileCheck {
        ANY(null),
        TRUE(true),
        FALSE(false);

        private final Boolean check;

        FileCheck(final Boolean check) {
            this.check = check;
        }

        public Boolean getCheck() {
            return check;
        }
    }
}
