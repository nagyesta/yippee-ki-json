package com.github.nagyesta.yippeekijson.metadata.schema.config;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.rule.JsonRule;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentType;
import com.github.nagyesta.yippeekijson.metadata.schema.markdown.DocumentationExporter;
import com.github.nagyesta.yippeekijson.metadata.schema.markdown.MarkdownGenerator;
import com.github.nagyesta.yippeekijson.metadata.schema.markdown.MarkdownGeneratorFactory;
import com.github.nagyesta.yippeekijson.metadata.schema.markdown.impl.*;
import com.github.nagyesta.yippeekijson.metadata.schema.parser.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Configuration of the metadata parser which will be used for schema and documentation generation.
 */
@Configuration
public class MetadataParserConfig {

    @Bean
    public NamedAnnotationMetadataParser<NamedRule> namedRuleMetadataParser() {
        return new NamedAnnotationMetadataParser<>(ComponentType.RULE, NamedRule::value, NamedRule.class);
    }

    @Bean
    public NamedAnnotationMetadataParser<NamedPredicate> namedPredicateMetadataParser() {
        return new NamedAnnotationMetadataParser<>(ComponentType.PREDICATE, NamedPredicate::value, NamedPredicate.class);
    }

    @Bean
    public NamedAnnotationMetadataParser<NamedFunction> namedFunctionMetadataParser() {
        return new NamedAnnotationMetadataParser<>(ComponentType.FUNCTION, NamedFunction::value, NamedFunction.class);
    }

    @Bean
    public NamedAnnotationMetadataParser<NamedSupplier> namedSupplierMetadataParser() {
        return new NamedAnnotationMetadataParser<>(ComponentType.SUPPLIER, NamedSupplier::value, NamedSupplier.class);
    }

    @Bean
    public WikiMetadataParser wikiMetadataParser() {
        return new WikiMetadataParser();
    }

    @Bean
    public ExampleMetadataParser exampleMetadataParser() {
        return new ExampleMetadataParser();
    }

    @Bean
    public DocumentationMetadataParser documentationMetadataParser() {
        return new DocumentationMetadataParser(wikiMetadataParser(), exampleMetadataParser());
    }

    @Bean
    public SchemaDefinitionMetadataParser schemaDefinitionMetadataParser() {
        return new SchemaDefinitionMetadataParser(documentationMetadataParser());
    }

    @Bean
    public ComponentContextMetadataParser componentContextMetadataParser() {
        return new ComponentContextMetadataParser(
                namedRuleMetadataParser(),
                namedPredicateMetadataParser(),
                namedFunctionMetadataParser(),
                namedSupplierMetadataParser(),
                schemaDefinitionMetadataParser());
    }

    @Bean
    public MarkdownGenerator jsonRuleMarkdownGenerator() {
        return new JsonRuleMarkdownGenerator();
    }

    @Bean
    public MarkdownGenerator supplierMarkdownGenerator() {
        return new SupplierMarkdownGenerator();
    }

    @Bean
    public MarkdownGenerator functionMarkdownGenerator() {
        return new FunctionMarkdownGenerator();
    }

    @Bean
    public MarkdownGenerator predicateMarkdownGenerator() {
        return new PredicateMarkdownGenerator();
    }

    @Bean
    @Primary
    public MarkdownGenerator markdownGeneratorFactory() {
        return new MarkdownGeneratorFactory(
                jsonRuleMarkdownGenerator(),
                supplierMarkdownGenerator(),
                functionMarkdownGenerator(),
                predicateMarkdownGenerator());
    }

    @Bean
    public DocumentationExporter documentationExporter(
            @Autowired @NotNull final List<Class<? extends JsonRule>> autoRegisterRules,
            @Autowired @NotNull final List<Class<? extends Supplier<?>>> autoRegisterSuppliers,
            @Autowired @NotNull final List<Class<? extends Function<?, ?>>> autoRegisterFunctions,
            @Autowired @NotNull final List<Class<? extends Predicate<Object>>> autoRegisterPredicates) {
        final DocumentationExporterImpl exporter = new DocumentationExporterImpl(
                componentContextMetadataParser(), markdownGeneratorFactory());
        exporter.setAutoRegisterRules(autoRegisterRules);
        exporter.setAutoRegisterFunctions(autoRegisterFunctions);
        exporter.setAutoRegisterPredicates(autoRegisterPredicates);
        exporter.setAutoRegisterSuppliers(autoRegisterSuppliers);
        return exporter;
    }
}
