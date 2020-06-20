package com.github.nagyesta.yippeekijson.core.config.entities;

import com.github.nagyesta.yippeekijson.core.annotation.Injectable;
import com.github.nagyesta.yippeekijson.core.config.validation.ValidYippeeConfig;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.filefilter.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.File;
import java.io.FileFilter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@ValidYippeeConfig
@Injectable(forType = RunConfig.class)
@Configuration
@ConfigurationProperties(prefix = "yippee", ignoreUnknownFields = false)
public class RunConfig {

    @NotBlank
    private String config;
    @NotBlank
    private String action;
    @NotBlank
    private String input;
    private String output;
    private String outputDirectory;
    private boolean allowOverwrite;
    private boolean relaxedYmlSchema;
    @NotNull
    private Charset charset = StandardCharsets.UTF_8;
    @NotNull
    @Size(min = 1)
    private List<String> includes;
    @NotNull
    private List<String> excludes;

    public RunConfig() {
    }

    private RunConfig(@org.jetbrains.annotations.NotNull final RunConfigBuilder builder) {
        this.config = builder.config;
        this.action = builder.action;
        this.input = builder.input;
        this.output = builder.output;
        this.outputDirectory = builder.outputDirectory;
        this.allowOverwrite = builder.allowOverwrite;
        this.relaxedYmlSchema = builder.relaxedYmlSchema;
        this.charset = builder.charset;
        this.includes = builder.includes;
        this.excludes = builder.excludes;
    }

    public static RunConfigBuilder builder() {
        return new RunConfigBuilder();
    }

    /**
     * Returns the configuration file as a file.
     *
     * @return the config file
     */
    public File getConfigAsFile() {
        return optionalFile(config);
    }

    /**
     * Returns the input file as a file.
     *
     * @return the input file
     */
    public File getInputAsFile() {
        return optionalFile(input);
    }

    /**
     * Returns the output file as a file. If the output is directory, outputDirectory is used, otherwise output.
     *
     * @return the output file
     */
    public File getOutputAsFile() {
        if (isOutputFileDirectory()) {
            return optionalFile(outputDirectory);
        } else {
            return optionalFile(output);
        }
    }

    /**
     * Performs a blank-check in outputDirectory.
     *
     * @return true is outputDirectory is set
     */
    public boolean isOutputFileDirectory() {
        return StringUtils.hasText(outputDirectory);
    }

    /**
     * Performs a blank-check in output.
     *
     * @return true is output is set
     */
    public boolean isOutputFileFile() {
        return StringUtils.hasText(output);
    }

    /**
     * Returns a {@link FileFilter} matching the includes but not matching directories or the excludes.
     *
     * @return filter
     */
    public FileFilter getWildcardFileFilter() {
        return new AndFileFilter(new WildcardFileFilter(includes),
                new NotFileFilter(new OrFileFilter(new WildcardFileFilter(excludes), DirectoryFileFilter.DIRECTORY)));
    }

    private File optionalFile(final String file) {
        return Optional.ofNullable(file)
                .map(File::new)
                .orElse(null);
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static class RunConfigBuilder {
        private String config;
        private String action;
        private String input;
        private String output;
        private String outputDirectory;
        private boolean allowOverwrite;
        private boolean relaxedYmlSchema;
        private Charset charset = StandardCharsets.UTF_8;
        private List<String> includes = Collections.emptyList();
        private List<String> excludes = Collections.emptyList();

        RunConfigBuilder() {
        }

        public RunConfigBuilder config(final String config) {
            this.config = config;
            return this;
        }

        public RunConfigBuilder action(final String action) {
            this.action = action;
            return this;
        }

        public RunConfigBuilder input(final String input) {
            this.input = input;
            return this;
        }

        public RunConfigBuilder output(final String output) {
            this.output = output;
            return this;
        }

        public RunConfigBuilder outputDirectory(final String outputDirectory) {
            this.outputDirectory = outputDirectory;
            return this;
        }

        public RunConfigBuilder allowOverwrite(final boolean allowOverwrite) {
            this.allowOverwrite = allowOverwrite;
            return this;
        }

        public RunConfigBuilder relaxedYmlSchema(final boolean relaxedYmlSchema) {
            this.relaxedYmlSchema = relaxedYmlSchema;
            return this;
        }

        public RunConfigBuilder charset(final Charset charset) {
            this.charset = charset;
            return this;
        }

        public RunConfigBuilder includes(final List<String> includes) {
            this.includes = includes;
            return this;
        }

        public RunConfigBuilder excludes(final List<String> excludes) {
            this.excludes = excludes;
            return this;
        }

        public RunConfig build() {
            return new RunConfig(this);
        }
    }
}

