package com.github.nagyesta.yippeekijson.core.config.validation;

import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class YippeeConfigValidator implements ConstraintValidator<ValidYippeeConfig, RunConfig> {

    private static final String FIELD_NAME_CONFIG = "config";
    private static final String FIELD_NAME_INPUT = "input";
    private static final String FIELD_NAME_OUTPUT = "output";
    private static final String FIELD_NAME_OUTPUT_DIR = "outputDirectory";
    private static final String FIELD_NAME_INCLUDES = "includes";

    public void initialize(final ValidYippeeConfig constraint) {
    }

    @Override
    public boolean isValid(final RunConfig obj, final ConstraintValidatorContext context) {
        //noinspection ConstantConditions
        return Optional.of(true)
                .map(valid -> verifyConfig(obj, context, valid))
                .map(valid -> verifyInput(obj, context, valid))
                .map(valid -> verifyOutputs(obj, context, valid))
                .map(valid -> verifyIncludes(obj, context, valid))
                .get();
    }

    /**
     * Verifies that the config file is valid within the validated object.
     *
     * @param obj     The validated object
     * @param context The validator context
     * @param valid   the true/false value of validity so far
     * @return false if invalid
     */
    protected boolean verifyConfig(final RunConfig obj, final ConstraintValidatorContext context, final boolean valid) {
        boolean result = valid;
        if (obj.getConfig() == null) {
            result = false;
        } else {
            final File config = obj.getConfigAsFile();
            if (verifyExists(context, config, FIELD_NAME_CONFIG)) {
                result = false;
            } else if (verifyCanRead(context, config, FIELD_NAME_CONFIG)) {
                result = false;
            } else if (verifyNotDirectory(context, config, FIELD_NAME_CONFIG)) {
                result = false;
            }
        }
        return result;
    }

    /**
     * Returns an optional file from the input.
     *
     * @param obj the config that contains the input file
     * @return the input wrapped with optional
     */
    protected Optional<File> getOptionalInput(final RunConfig obj) {
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
     * @param valid   the true/false value of validity so far
     * @return false if invalid
     */
    protected boolean verifyInput(final RunConfig obj, final ConstraintValidatorContext context, final boolean valid) {
        boolean result = valid;
        final Optional<File> input = getOptionalInput(obj);
        if (input.isPresent()) {
            if (verifyExists(context, input.get(), FIELD_NAME_INPUT)) {
                result = false;
            } else if (verifyCanRead(context, input.get(), FIELD_NAME_INPUT)) {
                result = false;
            }
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Verifies that the output file/directory is valid within the validated object.
     *
     * @param obj     The validated object
     * @param context The validator context
     * @param valid   the true/false value of validity so far
     * @return false if invalid
     */
    protected boolean verifyOutputs(final RunConfig obj, final ConstraintValidatorContext context, final boolean valid) {
        boolean result = valid;
        final boolean outputDirBlank = !StringUtils.hasText(obj.getOutputDirectory());
        final boolean outputBlank = !StringUtils.hasText(obj.getOutput());
        if (outputBlank && outputDirBlank) {
            addViolation(context, "Both 'output' and 'output-directory' parameters are blank. One of them must be set.");
            result = false;
        } else if (!outputBlank && !outputDirBlank) {
            addViolation(context, "Both 'output' and 'output-directory' parameters are set. Only one of them can be set at a time.");
            result = false;
        } else if (!outputBlank) {
            final File output = obj.getOutputAsFile();
            final Optional<File> input = getOptionalInput(obj);
            if (input.isPresent() && input.get().isDirectory()) {
                addPropertyViolation(context, FIELD_NAME_OUTPUT, "Input file is a directory but output isn't.");
                result = false;
            }
            if (output.exists() && verifyCanWrite(context, output, FIELD_NAME_OUTPUT)) {
                result = false;
            }
            if (output.exists() && verifyNotDirectory(context, output, FIELD_NAME_OUTPUT)) {
                result = false;
            }
        } else {
            final File output = obj.getOutputAsFile();
            if (output.exists()) {
                if (verifyDirectory(context, output, FIELD_NAME_OUTPUT_DIR)) {
                    result = false;
                } else if (verifyCanWrite(context, output, FIELD_NAME_OUTPUT_DIR)) {
                    result = false;
                }
            }
        }
        return result;
    }

    /**
     * Verifies that the inclusion patterns are valid within the validated object.
     *
     * @param obj     The validated object
     * @param context The validator context
     * @param valid   the true/false value of validity so far
     * @return false if invalid
     */
    protected boolean verifyIncludes(final RunConfig obj, final ConstraintValidatorContext context, final boolean valid) {
        boolean result = valid;
        if (obj.getIncludes() != null && obj.getIncludes().stream().anyMatch(Objects::isNull)) {
            addPropertyViolation(context, FIELD_NAME_INCLUDES, "Includes cannot contain null values.");
            result = false;
        }
        return result;
    }

    @SuppressWarnings("SameParameterValue")
    private boolean verifyDirectory(final ConstraintValidatorContext context, final File file, final String property) {
        return verifyTrue(context, property, file.isDirectory(), "The specified file is not a directory.");
    }

    private boolean verifyNotDirectory(final ConstraintValidatorContext context, final File file, final String property) {
        return verifyTrue(context, property, !file.isDirectory(), "The specified file is a directory.");
    }

    private boolean verifyExists(final ConstraintValidatorContext context, final File file, final String property) {
        return verifyTrue(context, property, file.exists(), "The specified file does not exist.");
    }

    private boolean verifyCanRead(final ConstraintValidatorContext context, final File file, final String property) {
        return verifyTrue(context, property, file.canRead(), "The specified file cannot be read.");
    }

    @SuppressWarnings("SameParameterValue")
    private boolean verifyCanWrite(final ConstraintValidatorContext context, final File file, final String property) {
        return verifyTrue(context, property, file.canWrite(), "The specified file cannot be written.");
    }

    private boolean verifyTrue(final ConstraintValidatorContext context, final String property, final boolean isTrue,
                               final String template) {
        if (!isTrue) {
            addPropertyViolation(context, property, template);
            return true;
        }
        return false;
    }

    private void addPropertyViolation(final ConstraintValidatorContext context, final String property, final String template) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(template).addPropertyNode(property).addConstraintViolation();
    }

    private void addViolation(final ConstraintValidatorContext context, final String template) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(template).addConstraintViolation();
    }
}
