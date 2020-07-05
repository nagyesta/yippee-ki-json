package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import com.github.nagyesta.yippeekijson.core.exception.ConfigValidationException;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;

public abstract class AbstractApplicationController implements ApplicationController {

    private final Validator validator;
    private final Logger log;

    public AbstractApplicationController(@NotNull final Validator validator, @NotNull final Logger log) {
        this.validator = validator;
        this.log = log;
    }

    @Override
    public void validateConfig(@Nullable final RunConfig runConfig) throws ConfigValidationException {
        if (runConfig == null) {
            this.log.error("RunConfig is null.");
            throw new ConfigValidationException("RunConfig is null.");
        }

        final Set<ConstraintViolation<RunConfig>> violations = validator.validate(runConfig, getValidationGroup());
        if (!CollectionUtils.isEmpty(violations)) {
            violations.forEach(v -> {
                if (v.getPropertyPath() != null && StringUtils.hasText(v.getPropertyPath().toString())) {
                    this.log.error("Config validation failure: yippee." + v.getPropertyPath() + ": " + v.getMessage());
                } else {
                    this.log.error("Config validation failure: yippee: " + v.getMessage());
                }
            });
            throw new ConfigValidationException("Validation failure.", violations);
        }
    }

    /**
     * Returns the validation group we will use to validate the config.
     *
     * @return the validation group.
     */
    @NotNull
    protected abstract Class<?> getValidationGroup();

    /**
     * Writes the given value to the provided file.
     *
     * @param value       the file we need to write to
     * @param charset     the file encoding we need to use
     * @param transformed the value we need to write
     * @throws IOException When the file cannot be written.
     */
    protected void writeToFile(@NotNull final File value,
                               @NotNull final Charset charset,
                               @NotNull final String transformed) throws IOException {
        FileUtils.write(value, transformed, charset, false);
    }
}
