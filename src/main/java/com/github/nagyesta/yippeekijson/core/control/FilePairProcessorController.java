package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.JsonAction;
import com.github.nagyesta.yippeekijson.core.config.entities.JsonActions;
import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import com.github.nagyesta.yippeekijson.core.config.parser.ActionConfigParser;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import com.github.nagyesta.yippeekijson.core.exception.ConfigValidationException;
import com.github.nagyesta.yippeekijson.core.exception.JsonTransformException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
public class FilePairProcessorController extends AbstractApplicationController implements ApplicationController {

    private final JsonTransformer jsonTransformer;
    private final FileSetTransformer fileSetTransformer;
    private final ActionConfigParser configParser;

    public FilePairProcessorController(@NotNull final JsonTransformer jsonTransformer,
                                       @NotNull final FileSetTransformer fileSetTransformer,
                                       @NotNull final ActionConfigParser configParser,
                                       @NotNull final Validator validator) {
        super(validator, log);
        this.jsonTransformer = jsonTransformer;
        this.fileSetTransformer = fileSetTransformer;
        this.configParser = configParser;
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
    @NotNull
    protected Class<RunConfig.Transform> getValidationGroup() {
        return RunConfig.Transform.class;
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
