package com.github.nagyesta.yippeekijson.core.config.entities;

import com.github.nagyesta.yippeekijson.core.annotation.Injectable;
import com.github.nagyesta.yippeekijson.core.config.validation.ValidFile;
import com.github.nagyesta.yippeekijson.core.config.validation.ValidYippeeConfig;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.filefilter.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.github.nagyesta.yippeekijson.core.config.validation.ValidFile.FileCheck.FALSE;
import static com.github.nagyesta.yippeekijson.core.config.validation.ValidFile.FileCheck.TRUE;

@Getter
@Setter
@ValidYippeeConfig(groups = RunConfig.Transform.class)
@Injectable(forType = RunConfig.class)
@Configuration
@ConfigurationProperties(prefix = "yippee", ignoreUnknownFields = false)
public class RunConfig {

    public interface Transform {
    }

    public interface ExportYaml {
    }

    public interface ExportMarkdown {
    }

    @NotBlank(groups = Transform.class)
    private String config;
    @NotBlank(groups = Transform.class)
    private String action;
    @NotBlank(groups = Transform.class)
    private String input;
    @Size(groups = ExportMarkdown.class, max = 0)
    @NotBlank(groups = ExportYaml.class)
    @ValidFile(groups = ExportYaml.class, isDirectory = FALSE, canWrite = TRUE)
    private String output;
    @Size(groups = ExportYaml.class, max = 0)
    @NotBlank(groups = ExportMarkdown.class)
    @ValidFile(groups = ExportMarkdown.class, isDirectory = TRUE, canWrite = TRUE)
    private String outputDirectory;
    private boolean allowOverwrite;
    private boolean relaxedYmlSchema;
    @AssertTrue(groups = ExportYaml.class)
    @AssertFalse(groups = {ExportMarkdown.class, Transform.class})
    private boolean exportYmlSchema;
    @AssertTrue(groups = ExportMarkdown.class)
    @AssertFalse(groups = {ExportYaml.class, Transform.class})
    private boolean exportMarkdown;
    @NotNull(groups = {Transform.class, ExportMarkdown.class, ExportYaml.class})
    private Charset charset;
    @NotNull(groups = Transform.class)
    @Size(min = 1, groups = Transform.class)
    private List<String> includes;
    @NotNull(groups = Transform.class)
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
        this.exportMarkdown = builder.exportMarkdown;
        this.exportYmlSchema = builder.exportYmlSchema;
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
        return new AndFileFilter(WildcardFileFilter.builder().setWildcards(includes).get(),
                new NotFileFilter(new OrFileFilter(WildcardFileFilter
                        .builder().setWildcards(excludes).get(), DirectoryFileFilter.DIRECTORY)));
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
        private boolean exportMarkdown = false;
        private boolean exportYmlSchema = false;
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

        public RunConfigBuilder exportYmlSchema(final boolean exportYmlSchema) {
            this.exportYmlSchema = exportYmlSchema;
            return this;
        }

        public RunConfigBuilder exportMarkdown(final boolean exportMarkdown) {
            this.exportMarkdown = exportMarkdown;
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

