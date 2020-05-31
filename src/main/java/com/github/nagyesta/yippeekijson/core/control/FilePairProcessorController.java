package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import com.github.nagyesta.yippeekijson.core.exception.ConfigValidationException;
import lombok.NonNull;

/**
 * Entry point to the file processing. Implementations contain the main logic of the operation.
 */
public interface FilePairProcessorController {

    /**
     * Starts processing as defined in the {@link RunConfig} parameter.
     *
     * @param runConfig The parameter containing the inputs for the file transformation.
     * @throws ConfigParseException      When the YML action config cannot be parsed.
     * @throws ConfigValidationException When the {@link RunConfig} is invalid.
     */
    void process(@NonNull RunConfig runConfig) throws ConfigParseException, ConfigValidationException;

    /**
     * Validates the {@link RunConfig} parameter.
     *
     * @param runConfig The parameter containing the inputs for the file transformation.
     * @throws ConfigValidationException When the {@link RunConfig} is invalid.
     */
    void validateConfig(RunConfig runConfig) throws ConfigValidationException;
}
