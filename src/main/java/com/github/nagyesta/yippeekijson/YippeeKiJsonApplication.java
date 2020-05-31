package com.github.nagyesta.yippeekijson;

import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import com.github.nagyesta.yippeekijson.core.control.FilePairProcessorController;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import com.github.nagyesta.yippeekijson.core.exception.ConfigValidationException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@Slf4j
public class YippeeKiJsonApplication {

    private static final int EXIT_CODE_BAD_ARGUMENTS = 1;
    private static final int EXIT_CODE_FAILED_TO_PRINT_HELP = 2;
    private static final int EXIT_CODE_CONFIG_PARSE_ERROR = 3;
    private static final int EXIT_CODE_FAILURE = 4;
    private final RunConfig runConfig;
    private final FilePairProcessorController controller;

    protected YippeeKiJsonApplication(@NonNull final RunConfig runConfig, @NonNull final FilePairProcessorController controller) {
        this.runConfig = runConfig;
        this.controller = controller;
    }

    /**
     * Entry point for the CLI app.
     *
     * @param args The command line arguments.
     */
    public static void main(final String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(YippeeKiJsonApplication.class, args);
        System.exit(context.getBean(YippeeKiJsonApplication.class).run());
    }

    /**
     * Executes the application.
     *
     * @return The exit code we need to use to exit.
     */
    protected int run() {
        int exitCode = 0;
        try {
            this.controller.process(runConfig);
        } catch (final ConfigValidationException e) {
            try {
                printHelp();
                exitCode = EXIT_CODE_BAD_ARGUMENTS;
            } catch (final IOException ioException) {
                log.error(ioException.getMessage(), e);
                exitCode = EXIT_CODE_FAILED_TO_PRINT_HELP;
            }
        } catch (final ConfigParseException e) {
            log.error(e.getMessage(), e);
            exitCode = EXIT_CODE_CONFIG_PARSE_ERROR;
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            exitCode = EXIT_CODE_FAILURE;
        }
        return exitCode;
    }

    /**
     * Prints the help file to the Standard Error channel.
     *
     * @throws IOException In case the help message cannot be read.
     */
    protected void printHelp() throws IOException {
        System.err.println(IOUtils.resourceToString("/help.txt", StandardCharsets.UTF_8));
    }

}

