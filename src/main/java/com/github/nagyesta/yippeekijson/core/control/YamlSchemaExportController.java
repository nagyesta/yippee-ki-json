package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import com.github.nagyesta.yippeekijson.core.exception.ConfigValidationException;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaExporter;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * Yaml schema generation specific implementation of {@link ApplicationController}.
 */
@Slf4j
public class YamlSchemaExportController extends AbstractApplicationController {

    private final JsonSchemaExporter jsonSchemaExporter;

    public YamlSchemaExportController(@NotNull final JsonSchemaExporter jsonSchemaExporter,
                                      @NotNull final Validator validator) {
        super(validator, log);
        this.jsonSchemaExporter = jsonSchemaExporter;
    }

    @Override
    public void process(@NotNull final RunConfig runConfig) throws ConfigParseException, ConfigValidationException {
        validateConfig(runConfig);
        try {
            final String schema = jsonSchemaExporter.exportSchema();
            writeToFile(runConfig.getOutputAsFile(), runConfig.getCharset(), schema);
        } catch (final Exception e) {
            throw new IllegalStateException("Couldn't generate schema: " + e.getMessage(), e);
        }
    }

    @Override
    @NotNull
    protected Class<?> getValidationGroup() {
        return RunConfig.ExportYaml.class;
    }

}
