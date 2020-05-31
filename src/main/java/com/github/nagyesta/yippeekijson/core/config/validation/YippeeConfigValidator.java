package com.github.nagyesta.yippeekijson.core.config.validation;

import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import lombok.NonNull;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class YippeeConfigValidator implements ConstraintValidator<ValidYippeeConfig, RunConfig> {

    private static final String FIELD_NAME_OUTPUT = "output";
    private static final String FIELD_NAME_INCLUDES = "includes";

    private final FileValidator configValidator;
    private final FileValidator inputValidator;
    private final FileValidator outputFileValidator;
    private final FileValidator outputDirectoryValidator;

    public YippeeConfigValidator(@NonNull final FileValidator configValidator, @NonNull final FileValidator inputValidator,
                                 @NonNull final FileValidator outputFileValidator, @NonNull final FileValidator outputDirectoryValidator) {
        this.configValidator = configValidator;
        this.inputValidator = inputValidator;
        this.outputFileValidator = outputFileValidator;
        this.outputDirectoryValidator = outputDirectoryValidator;
    }

    public void initialize(final ValidYippeeConfig constraint) {
    }

    @Override
    public boolean isValid(@NonNull final RunConfig obj, @NonNull final ConstraintValidatorContext context) {
        //noinspection ConstantConditions
        return Optional.of(true)
                .map(v -> v & verifyConfig(obj, context))
                .map(v -> v & verifyInput(obj, context))
                .map(v -> v & verifyOutputs(obj, context))
                .map(v -> v & verifyIncludes(obj, context))
                .get();
    }

    /**
     * Verifies that the config file is valid within the validated object.
     *
     * @param obj     The validated object
     * @param context The validator context
     * @return false if invalid
     */
    protected boolean verifyConfig(@NonNull final RunConfig obj, @NonNull final ConstraintValidatorContext context) {
        boolean result;
        if (obj.getConfig() == null) {
            result = false;
        } else {
            result = configValidator.isValid(obj.getConfigAsFile(), context);
        }
        return result;
    }

    /**
     * Returns an optional file from the input.
     *
     * @param obj the config that contains the input file
     * @return the input wrapped with optional
     */
    protected Optional<File> getOptionalInput(@NonNull final RunConfig obj) {
        if (obj.getInput() == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(obj.getInputAsFile());
        }
    }

    /**
     * Verifies that the input file is valid within the validated object.
     *
     * @param obj     The validated object
     * @param context The validator context
     * @return false if invalid
     */
    protected boolean verifyInput(@NonNull final RunConfig obj, @NonNull final ConstraintValidatorContext context) {
        return getOptionalInput(obj)
                .filter(file -> inputValidator.isValid(file, context))
                .isPresent();
    }

    /**
     * Verifies that the output file/directory is valid within the validated object.
     *
     * @param obj     The validated object
     * @param context The validator context
     * @return false if invalid
     */
    protected boolean verifyOutputs(@NonNull final RunConfig obj, @NonNull final ConstraintValidatorContext context) {
        boolean result = true;
        if (areBothOutputsSetOrBothMissing(obj)) {
            addPropertyViolation(context, null, "Exactly one of 'output' or 'output-directory' parameters must be set.");
            result = false;
        }
        result &= verifyOutputFile(obj, context);
        result &= verifyOutputDirectory(obj, context);

        return result;
    }

    /**
     * Verifies that the inclusion patterns are valid within the validated object.
     *
     * @param obj     The validated object
     * @param context The validator context
     * @return false if invalid
     */
    protected boolean verifyIncludes(@NonNull final RunConfig obj, @NonNull final ConstraintValidatorContext context) {
        boolean result = true;
        if (obj.getIncludes() != null && obj.getIncludes().stream().anyMatch(Objects::isNull)) {
            addPropertyViolation(context, FIELD_NAME_INCLUDES, "Includes cannot contain null values.");
            result = false;
        }
        return result;
    }

    private boolean areBothOutputsSetOrBothMissing(final RunConfig obj) {
        final boolean outputDirBlank = !StringUtils.hasText(obj.getOutputDirectory());
        final boolean outputBlank = !StringUtils.hasText(obj.getOutput());
        return (outputBlank && outputDirBlank) || (!outputBlank && !outputDirBlank);
    }

    private boolean verifyOutputFile(final RunConfig obj, final ConstraintValidatorContext context) {
        boolean result = true;
        if (obj.isOutputFileFile()) {
            final Optional<File> input = getOptionalInput(obj);
            if (input.isPresent() && input.get().isDirectory()) {
                addPropertyViolation(context, FIELD_NAME_OUTPUT, "Input file is a directory but output isn't.");
                result = false;
            }
            result &= validateOutputWith(obj, context, this.outputFileValidator);
        }
        return result;
    }

    private boolean verifyOutputDirectory(final RunConfig obj, final ConstraintValidatorContext context) {
        boolean result = true;
        if (obj.isOutputFileDirectory()) {
            result = validateOutputWith(obj, context, this.outputDirectoryValidator);
        }
        return result;
    }

    private boolean validateOutputWith(final RunConfig obj, final ConstraintValidatorContext context, final FileValidator fileValidator) {
        boolean result = true;
        final File output = obj.getOutputAsFile();
        if (output.exists()) {
            result = fileValidator.isValid(output, context);
        }
        return result;
    }

    private void addPropertyViolation(final ConstraintValidatorContext context, final String property, final String template) {
        context.disableDefaultConstraintViolation();
        final ConstraintValidatorContext.ConstraintViolationBuilder builder = context.buildConstraintViolationWithTemplate(template);
        if (StringUtils.hasText(property)) {
            builder.addPropertyNode(property).addConstraintViolation();
        } else {
            builder.addConstraintViolation();
        }
    }
}
