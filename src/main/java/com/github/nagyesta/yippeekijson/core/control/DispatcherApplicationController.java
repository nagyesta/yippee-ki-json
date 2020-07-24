package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import com.github.nagyesta.yippeekijson.core.exception.ConfigValidationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Primary implementation of {@link ApplicationController}.
 * Automatically selects from the wrapped implementations.
 */
public class DispatcherApplicationController implements ApplicationController {

    private final ApplicationController filePairProcessorController;
    private final ApplicationController markdownExportController;
    private final ApplicationController yamlSchemaExportController;

    public DispatcherApplicationController(@NotNull final ApplicationController filePairProcessorController,
                                           @NotNull final ApplicationController markdownExportController,
                                           @NotNull final ApplicationController yamlSchemaExportController) {
        this.filePairProcessorController = filePairProcessorController;
        this.markdownExportController = markdownExportController;
        this.yamlSchemaExportController = yamlSchemaExportController;
    }

    @Override
    public void process(@NotNull final RunConfig runConfig) throws ConfigParseException, ConfigValidationException {
        selectApplicationController(runConfig).process(runConfig);
    }

    @Override
    public void validateConfig(@Nullable final RunConfig runConfig) throws ConfigValidationException {
        selectApplicationController(runConfig).validateConfig(runConfig);
    }

    private ApplicationController selectApplicationController(@Nullable final RunConfig runConfig) {
        ApplicationController controller;
        if (runConfig == null || runConfig.isExportMarkdown()) {
            controller = this.markdownExportController;
        } else if (runConfig.isExportYmlSchema()) {
            controller = this.yamlSchemaExportController;
        } else {
            controller = this.filePairProcessorController;
        }
        return controller;
    }
}
