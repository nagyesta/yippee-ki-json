package com.github.nagyesta.yippeekijson.metadata.schema.entity;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Value object to represent documentation specific information of named components.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Getter
public final class DocumentationContext {

    private final URI wikiReference;

    private final String fileName;

    private final String sectionTitle;

    private final Optional<Type> inputType;

    private final Optional<Type> outputType;

    private final String sinceVersion;

    private final List<String> description;

    private final Optional<ClassPathResource> exampleInput;

    private final Optional<ClassPathResource> exampleOutput;

    private final Optional<ClassPathResource> exampleConfig;

    private final List<String> exampleNote;

    private final boolean skipTest;

    private DocumentationContext(final DocumentationContextBuilder builder) {
        this.wikiReference = builder.wikiReference;
        this.fileName = builder.fileName;
        this.sectionTitle = builder.sectionTitle;
        this.inputType = builder.inputType;
        this.outputType = builder.outputType;
        this.sinceVersion = builder.sinceVersion;
        this.description = List.copyOf(builder.description);
        this.exampleInput = builder.exampleInput;
        this.exampleOutput = builder.exampleOutput;
        this.exampleConfig = builder.exampleConfig;
        this.exampleNote = List.copyOf(builder.exampleNote);
        this.skipTest = builder.skipTest;
    }

    public static DocumentationContextBuilder builder() {
        return new DocumentationContextBuilder();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static class DocumentationContextBuilder {
        private URI wikiReference;
        private String fileName;
        private String sectionTitle;
        private Optional<Type> inputType;
        private Optional<Type> outputType;
        private String sinceVersion;
        private List<String> description;
        private Optional<ClassPathResource> exampleInput;
        private Optional<ClassPathResource> exampleOutput;
        private Optional<ClassPathResource> exampleConfig;
        private List<String> exampleNote;
        private boolean skipTest;

        DocumentationContextBuilder() {
            reset();
        }

        private void reset() {
            this.wikiReference = null;
            this.fileName = null;
            this.sectionTitle = null;
            this.inputType = Optional.empty();
            this.outputType = Optional.empty();
            this.sinceVersion = null;
            this.description = new ArrayList<>();
            this.exampleInput = Optional.empty();
            this.exampleOutput = Optional.empty();
            this.exampleConfig = Optional.empty();
            this.exampleNote = null;
            this.skipTest = false;
        }

        public DocumentationContextBuilder wikiReference(@NotNull final URI wikiReference) {
            this.wikiReference = wikiReference;
            return this;
        }

        public DocumentationContextBuilder fileName(@NotNull final String fileName) {
            this.fileName = fileName;
            return this;
        }

        public DocumentationContextBuilder sectionTitle(@NotNull final String sectionTitle) {
            this.sectionTitle = sectionTitle;
            return this;
        }

        public DocumentationContextBuilder inputType(@NotNull final Optional<Type> inputType) {
            this.inputType = inputType;
            return this;
        }

        public DocumentationContextBuilder outputType(@NotNull final Optional<Type> outputType) {
            this.outputType = outputType;
            return this;
        }

        public DocumentationContextBuilder sinceVersion(@NotNull final String sinceVersion) {
            this.sinceVersion = sinceVersion;
            return this;
        }

        public DocumentationContextBuilder description(@NotNull final List<String> description) {
            this.description = description;
            return this;
        }

        public DocumentationContextBuilder exampleInput(@NotNull final Optional<ClassPathResource> exampleInput) {
            this.exampleInput = exampleInput;
            return this;
        }

        public DocumentationContextBuilder exampleOutput(@NotNull final Optional<ClassPathResource> exampleOutput) {
            this.exampleOutput = exampleOutput;
            return this;
        }

        public DocumentationContextBuilder exampleConfig(@NotNull final Optional<ClassPathResource> exampleConfig) {
            this.exampleConfig = exampleConfig;
            return this;
        }

        public DocumentationContextBuilder exampleNote(@NotNull final List<String> exampleNote) {
            this.exampleNote = List.copyOf(exampleNote);
            return this;
        }

        public DocumentationContextBuilder skipTest(final boolean skipTest) {
            this.skipTest = skipTest;
            return this;
        }

        public DocumentationContext build() {
            final DocumentationContext documentationContext = new DocumentationContext(this);
            this.reset();
            return documentationContext;
        }
    }
}
