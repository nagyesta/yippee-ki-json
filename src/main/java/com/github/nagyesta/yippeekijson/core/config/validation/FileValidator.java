package com.github.nagyesta.yippeekijson.core.config.validation;

import jakarta.validation.ConstraintValidatorContext;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Provides a reusable way for validating files.
 */
public class FileValidator {

    private final String propertyPath;

    private final Boolean exists;

    private final Boolean canRead;

    private final Boolean canWrite;

    private final Boolean isDirectory;

    public FileValidator(@Nullable final String propertyPath,
                         @Nullable final Boolean exists,
                         @Nullable final Boolean canRead,
                         @Nullable final Boolean canWrite,
                         @Nullable final Boolean isDirectory) {
        this.propertyPath = propertyPath;
        this.exists = exists;
        this.canRead = canRead;
        this.canWrite = canWrite;
        this.isDirectory = isDirectory;
    }

    /**
     * Validates the provided file and reports violations based on the config of this instance.
     *
     * @param obj      The file we are validating
     * @param context  The context where we report violations
     * @param messages The messages we want to use when we report violations
     * @return true if the file was valid, false otherwise.
     */
    public boolean isValid(@NonNull final File obj,
                           @NonNull final ConstraintValidatorContext context,
                           @NonNull final Map<YippeeConfigValidator.FailureReasonCode, String> messages) {
        //noinspection ConstantConditions
        return Optional.of(true)
                .map(v -> v & testFile(this.exists, obj, context, File::exists,
                        messages.get(YippeeConfigValidator.FailureReasonCode.FILE_DOES_NOT_EXIST),
                        messages.get(YippeeConfigValidator.FailureReasonCode.FILE_EXISTS)))
                .map(v -> v & testFile(this.canRead, obj, context, File::canRead,
                        messages.get(YippeeConfigValidator.FailureReasonCode.FILE_CANNOT_BE_READ),
                        messages.get(YippeeConfigValidator.FailureReasonCode.FILE_CAN_BE_READ)))
                .map(v -> v & testFile(this.canWrite, obj, context, File::canWrite,
                        messages.get(YippeeConfigValidator.FailureReasonCode.FILE_CANNOT_BE_WRITTEN),
                        messages.get(YippeeConfigValidator.FailureReasonCode.FILE_CAN_BE_WRITTEN)))
                .map(v -> v & testFile(this.isDirectory, obj, context, File::isDirectory,
                        messages.get(YippeeConfigValidator.FailureReasonCode.FILE_IS_NOT_A_DIRECTORY),
                        messages.get(YippeeConfigValidator.FailureReasonCode.FILE_IS_A_DIRECTORY)))
                .get();
    }

    private boolean testFile(final Boolean shouldMatch, final File obj, final ConstraintValidatorContext context,
                             final Predicate<File> testForTrue, final String trueTemplate, final String falseTemplate) {
        boolean result = true;
        if (shouldMatch != null) {
            if (shouldMatch) {
                result = validIfTrue(context, propertyPath, testForTrue.test(obj), trueTemplate);
            } else {
                result = validIfTrue(context, propertyPath, !testForTrue.test(obj), falseTemplate);
            }
        }
        return result;
    }

    private boolean validIfTrue(final ConstraintValidatorContext context, final String property, final boolean isTrue,
                                final String template) {
        if (!isTrue) {
            context.disableDefaultConstraintViolation();
            if (property != null) {
                context.buildConstraintViolationWithTemplate(template).addPropertyNode(property).addConstraintViolation();
            } else {
                context.buildConstraintViolationWithTemplate(template).addConstraintViolation();
            }
            return false;
        }
        return true;
    }

}
