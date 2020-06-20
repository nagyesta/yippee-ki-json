package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.JsonAction;
import com.github.nagyesta.yippeekijson.core.config.entities.JsonActions;
import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import com.github.nagyesta.yippeekijson.core.config.parser.ActionConfigParser;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import com.github.nagyesta.yippeekijson.core.exception.ConfigValidationException;
import com.github.nagyesta.yippeekijson.core.exception.JsonTransformException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FilePairProcessorControllerImpl implements FilePairProcessorController {

    private final JsonTransformer jsonTransformer;
    private final FileSetTransformer fileSetTransformer;
    private final ActionConfigParser configParser;
    private final Validator validator;

    @Autowired
    public FilePairProcessorControllerImpl(@NonNull final JsonTransformer jsonTransformer,
                                           @NonNull final FileSetTransformer fileSetTransformer,
                                           @NonNull final ActionConfigParser configParser,
                                           @NonNull final Validator validator) {
        this.jsonTransformer = jsonTransformer;
        this.fileSetTransformer = fileSetTransformer;
        this.configParser = configParser;
        this.validator = validator;
    }

    @Override
    public void process(@NotNull final RunConfig runConfig) throws ConfigParseException, ConfigValidationException {
        validateConfig(runConfig);

        final JsonActions actions = configParser.parse(runConfig.getConfigAsFile(), runConfig.isRelaxedYmlSchema());
        final JsonAction jsonAction = actions.getActions().get(runConfig.getAction());
        Assert.notNull(jsonAction, "No action found: " + runConfig.getAction());

        final Map<File, File> toDoMap = fileSetTransformer.transformToFilePairs(runConfig);
        final Map<File, File> success = new TreeMap<>();
        final Map<File, File> failure = new TreeMap<>();

        toDoMap.forEach((key, value) -> {
            try {
                if (value.exists() && !runConfig.isAllowOverwrite()) {
                    failure.put(key, value);
                    log.warn("Overwrite is not allowed: " + value);
                    return;
                }
                writeToFile(value, runConfig.getCharset(),
                        jsonTransformer.transform(key, runConfig.getCharset(), jsonAction));
                success.put(key, value);
            } catch (final JsonTransformException | IOException e) {
                failure.put(key, value);
                log.error("Failed to process file: " + value + " due to: " + e.getMessage());
            }
        });

        logResults(success, failure);
    }

    @Override
    public void validateConfig(@Nullable final RunConfig runConfig) throws ConfigValidationException {
        if (runConfig == null) {
            log.error("RunConfig is null.");
            throw new ConfigValidationException("RunConfig is null.");
        }

        final Set<ConstraintViolation<RunConfig>> violations = validator.validate(runConfig);
        if (!CollectionUtils.isEmpty(violations)) {
            violations.forEach(v -> {
                if (v.getPropertyPath() != null && StringUtils.hasText(v.getPropertyPath().toString())) {
                    log.error("Config validation failure: yippee." + v.getPropertyPath() + ": " + v.getMessage());
                } else {
                    log.error("Config validation failure: yippee: " + v.getMessage());
                }
            });
            throw new ConfigValidationException("Validation failure.", violations);
        }
    }

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

    /**
     * Summarizes conversion result maps into a String format for logging.
     *
     * @param map The result map
     * @return The String formatted representation
     */
    protected String summarize(@NotNull final Map<File, File> map) {
        return map.entrySet().stream()
                .map(e -> e.getKey() + "\n -> " + e.getValue())
                .collect(Collectors.joining("\n"));
    }

    private void logResults(final Map<File, File> success, final Map<File, File> failure) {
        if (log.isInfoEnabled()) {
            final String successResults = summarize(success);
            final String failureResults = summarize(failure);
            log.info("Conversion completed.\nSuccess:\n" + successResults + "\nFailed:\n" + failureResults);
        }
    }
}
