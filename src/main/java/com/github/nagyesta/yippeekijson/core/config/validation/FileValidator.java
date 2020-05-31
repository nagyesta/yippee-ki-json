package com.github.nagyesta.yippeekijson.core.config.validation;

import lombok.NonNull;

import javax.validation.ConstraintValidatorContext;
import java.io.File;
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

    public FileValidator(@NonNull final String propertyPath, final Boolean exists, final Boolean canRead,
                         final Boolean canWrite, final Boolean isDirectory) {
        this.propertyPath = propertyPath;
        this.exists = exists;
        this.canRead = canRead;
        this.canWrite = canWrite;
        this.isDirectory = isDirectory;
    }

    /**
     * Validates the provided file and reports violations based on the config of this instance.
     *
     * @param obj     The file we are validating
     * @param context The context wher ewe report violations
     * @return true if the file was valid, false otherwise.
     */
    public boolean isValid(@NonNull final File obj, @NonNull final ConstraintValidatorContext context) {
        //noinspection ConstantConditions
        return Optional.of(true)
                .map(v -> v & testFile(this.exists, obj, context, File::exists,
                        "The specified file does not exist.",
                        "The specified file exists but it shouldn't."))
                .map(v -> v & testFile(this.canRead, obj, context, File::canRead,
                        "The specified file cannot be read.",
                        "The specified file can be read but it shouldn't."))
                .map(v -> v & testFile(this.canWrite, obj, context, File::canWrite,
                        "The specified file cannot be written.",
                        "The specified file can be written but it shouldn't."))
                .map(v -> v & testFile(this.isDirectory, obj, context, File::isDirectory,
                        "The specified file is not a directory.",
                        "The specified file is a directory but it shouldn't."))
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
            context.buildConstraintViolationWithTemplate(template).addPropertyNode(property).addConstraintViolation();
            return false;
        }
        return true;
    }

}
