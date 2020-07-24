package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import com.github.nagyesta.yippeekijson.core.exception.ConfigValidationException;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentType;
import com.github.nagyesta.yippeekijson.metadata.schema.markdown.DocumentationExporter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.validation.Validator;
import java.io.File;
import java.util.Map;

/**
 * Documentation generation specific implementation of {@link ApplicationController}.
 */
@Slf4j
public class MarkdownExportController extends AbstractApplicationController {

    private final DocumentationExporter documentationExporter;

    public MarkdownExportController(@NotNull final DocumentationExporter documentationExporter,
                                    @NotNull final Validator validator) {
        super(validator, log);
        this.documentationExporter = documentationExporter;
    }

    @Override
    public void process(@NotNull final RunConfig runConfig) throws ConfigParseException, ConfigValidationException {
        validateConfig(runConfig);
        try {
            for (final ComponentType componentType : ComponentType.values()) {
                final Map<String, String> files = documentationExporter.exportDocumentation(componentType);
                for (final Map.Entry<String, String> entry : files.entrySet()) {
                    File output = new File(runConfig.getOutputAsFile(), entry.getKey());
                    writeToFile(output, runConfig.getCharset(), entry.getValue());
                }
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Couldn't generate markdown: " + e.getMessage(), e);
        }
    }

    @Override
    @NotNull
    protected Class<?> getValidationGroup() {
        return RunConfig.ExportMarkdown.class;
    }

}
