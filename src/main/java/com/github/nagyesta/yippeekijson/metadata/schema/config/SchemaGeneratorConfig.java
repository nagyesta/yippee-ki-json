package com.github.nagyesta.yippeekijson.metadata.schema.config;

import com.github.nagyesta.yippeekijson.core.rule.JsonRule;
import com.github.nagyesta.yippeekijson.metadata.schema.CommonTypeConfig;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.CompositeTypeDefinitionRegistry;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonCommonTypeDefinitionRegistry;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonNamedComponentTypeDefinitionRegistry;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaExporter;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.NamedComponentConverter;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.PropertyContextPreprocessor;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.impl.JsonRuleSchemaTypeConverter;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.impl.NamedFunctionalComponentConverter;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.impl.PropertyContextPreprocessorImpl;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.exporter.JsonSchemaExporterImpl;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.registry.*;
import com.github.nagyesta.yippeekijson.metadata.schema.parser.ComponentContextMetadataParser;
import com.github.nagyesta.yippeekijson.metadata.schema.parser.WikiMetadataParser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Configuration of the JSON schema generator components.
 */
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Configuration
public class SchemaGeneratorConfig {

    @Autowired
    private ComponentContextMetadataParser componentContextMetadataParser;
    @Autowired
    private WikiMetadataParser wikiMetadataParser;

    @Bean
    public JsonCommonTypeDefinitionRegistry jsonCommonTypeDefinitionRegistry() {
        final JsonCommonTypeDefinitionRegistry registry = new JsonCommonTypeDefinitionRegistryImpl(
                this.wikiMetadataParser
        );
        CommonTypeConfig.registerTo(registry);
        return registry;
    }

    @Bean
    public JsonNamedComponentTypeDefinitionRegistry<JsonRule> jsonRuleTypeDefinitionRegistry() {
        return new JsonRuleComponentTypeDefinitionRegistry(
                this.componentContextMetadataParser,
                namedRuleComponentConverter(),
                jsonCommonTypeDefinitionRegistry()
        );
    }

    @Bean
    public PropertyContextPreprocessor propertyContextPreprocessor() {
        return new PropertyContextPreprocessorImpl();
    }

    @Bean
    public NamedComponentConverter namedRuleComponentConverter() {
        return new JsonRuleSchemaTypeConverter(propertyContextPreprocessor());
    }

    @Bean
    public NamedComponentConverter namedFunctionalComponentConverter() {
        return new NamedFunctionalComponentConverter(propertyContextPreprocessor());
    }

    @Bean
    public JsonNamedComponentTypeDefinitionRegistry<Supplier<?>> supplierTypeDefinitionRegistry() {
        return new SupplierComponentTypeDefinitionRegistry(
                this.componentContextMetadataParser,
                namedFunctionalComponentConverter()
        );
    }

    @Bean
    public JsonNamedComponentTypeDefinitionRegistry<Predicate<?>> predicateTypeDefinitionRegistry() {
        return new PredicateComponentTypeDefinitionRegistry(
                this.componentContextMetadataParser,
                namedFunctionalComponentConverter()
        );
    }

    @Bean
    public JsonNamedComponentTypeDefinitionRegistry<Function<?, ?>> functionTypeDefinitionRegistry() {
        return new FunctionComponentTypeDefinitionRegistry(
                this.componentContextMetadataParser,
                namedFunctionalComponentConverter()
        );
    }

    @Bean
    public CompositeTypeDefinitionRegistry compositeTypeDefinitionRegistry() {
        CompositeTypeDefinitionRegistry bean = new CompositeTypeDefinitionRegistryImpl(
                jsonCommonTypeDefinitionRegistry(),
                supplierTypeDefinitionRegistry(),
                predicateTypeDefinitionRegistry(),
                functionTypeDefinitionRegistry(),
                jsonRuleTypeDefinitionRegistry()
        );
        namedRuleComponentConverter().setTypeDefinitionRegistry(bean);
        namedFunctionalComponentConverter().setTypeDefinitionRegistry(bean);
        return bean;
    }

    @Bean
    public JsonSchemaExporter jsonSchemaExporter(
            @Autowired @NotNull final List<Class<? extends JsonRule>> autoRegisterRules,
            @Autowired @NotNull final List<Class<? extends Supplier<?>>> autoRegisterSuppliers,
            @Autowired @NotNull final List<Class<? extends Function<?, ?>>> autoRegisterFunctions,
            @Autowired @NotNull final List<Class<? extends Predicate<Object>>> autoRegisterPredicates) {
        final JsonSchemaExporterImpl exporter = new JsonSchemaExporterImpl(
                jsonCommonTypeDefinitionRegistry(),
                supplierTypeDefinitionRegistry(),
                predicateTypeDefinitionRegistry(),
                functionTypeDefinitionRegistry(),
                jsonRuleTypeDefinitionRegistry()
        );
        exporter.setAutoRegisterRules(autoRegisterRules);
        exporter.setAutoRegisterFunctions(autoRegisterFunctions);
        exporter.setAutoRegisterPredicates(autoRegisterPredicates);
        exporter.setAutoRegisterSuppliers(autoRegisterSuppliers);
        return exporter;
    }
}
