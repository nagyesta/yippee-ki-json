package com.github.nagyesta.yippeekijson.metadata.schema.definitions.exporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.nagyesta.yippeekijson.core.rule.JsonRule;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonCommonTypeDefinitionRegistry;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonNamedComponentTypeDefinitionRegistry;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaExporter;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaObject;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.*;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonCommonTypeDefinitionRegistry.COMMON_TYPES;
import static com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonNamedComponentTypeDefinitionRegistry.*;
import static com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.CommonLiteralType.description;
import static com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonRefSchemaTypeDefinition.ref;

public class JsonSchemaExporterImpl implements JsonSchemaExporter {

    private final JsonCommonTypeDefinitionRegistry commonTypeDefinitionRegistry;
    private final JsonNamedComponentTypeDefinitionRegistry<JsonRule> jsonRuleTypeDefinitionRegistry;
    private final JsonNamedComponentTypeDefinitionRegistry<Supplier<?>> supplierTypeDefinitionRegistry;
    private final JsonNamedComponentTypeDefinitionRegistry<Predicate<?>> predicateTypeDefinitionRegistry;
    private final JsonNamedComponentTypeDefinitionRegistry<Function<?, ?>> functionTypeDefinitionRegistry;

    public JsonSchemaExporterImpl(
            @NotNull final JsonCommonTypeDefinitionRegistry commonTypeDefinitionRegistry,
            @NotNull final JsonNamedComponentTypeDefinitionRegistry<Supplier<?>> supplierTypeDefinitionRegistry,
            @NotNull final JsonNamedComponentTypeDefinitionRegistry<Predicate<?>> predicateTypeDefinitionRegistry,
            @NotNull final JsonNamedComponentTypeDefinitionRegistry<Function<?, ?>> functionTypeDefinitionRegistry,
            @NotNull final JsonNamedComponentTypeDefinitionRegistry<JsonRule> jsonRuleTypeDefinitionRegistry) {
        this.commonTypeDefinitionRegistry = commonTypeDefinitionRegistry;
        this.supplierTypeDefinitionRegistry = supplierTypeDefinitionRegistry;
        this.predicateTypeDefinitionRegistry = predicateTypeDefinitionRegistry;
        this.functionTypeDefinitionRegistry = functionTypeDefinitionRegistry;
        this.jsonRuleTypeDefinitionRegistry = jsonRuleTypeDefinitionRegistry;
    }

    @Override
    public String exportSchema() throws JsonProcessingException {
        ObjectWriter objectWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();
        final SchemaRootType schema = SchemaRootType.builder()
                .schemaRef(WikiConstants.SCHEMA_URL)
                .comment(WikiConstants.WIKI_CONFIG_URL)
                .addDefinitions(COMMON_TYPES, commonTypes())
                .addDefinitions(FUNCTION_TYPES, functionTypes())
                .addDefinitions(PREDICATE_TYPES, predicateTypes())
                .addDefinitions(SUPPLIER_TYPES, supplierTypes())
                .addDefinitions(RULE_TYPES, ruleTypes())
                .addDefinitions("actionTypes", actionTypeDefinitions())
                .addRequiredProperty("actions", ref("#/definitions/actionTypes/definitions/actions"))
                .disallowAdditionalProperties()
                .build();
        return objectWriter.writeValueAsString(schema);
    }

    @NotNull
    private JsonDefinitionsType commonTypes() {
        return JsonDefinitionsType.builder()
                .description("Some of the reusable base types")
                .addAll(this.commonTypeDefinitionRegistry.registeredDefinitions())
                .build();
    }

    @NotNull
    private JsonDefinitionsType functionTypes() {
        return JsonDefinitionsType.builder()
                .comment(WikiConstants.CONTENT_ROOT + "Built-in-functions")
                .description("Definition of known function types")
                .addAll(this.functionTypeDefinitionRegistry.registeredDefinitions())
                .build();
    }

    @NotNull
    private JsonDefinitionsType predicateTypes() {
        return JsonDefinitionsType.builder()
                .comment(WikiConstants.CONTENT_ROOT + "Built-in-predicates")
                .description("Definition of known predicate types")
                .addAll(this.predicateTypeDefinitionRegistry.registeredDefinitions())
                .build();
    }

    @NotNull
    private JsonDefinitionsType supplierTypes() {
        return JsonDefinitionsType.builder()
                .comment(WikiConstants.CONTENT_ROOT + "Built-in-suppliers")
                .description("Definition of known supplier types")
                .addAll(this.supplierTypeDefinitionRegistry.registeredDefinitions())
                .build();
    }

    @NotNull
    private JsonDefinitionsType ruleTypes() {
        return JsonDefinitionsType.builder()
                .comment(WikiConstants.CONTENT_ROOT + "Built-in-rules")
                .description("Definition of known rule types")
                .addAll(this.jsonRuleTypeDefinitionRegistry.registeredDefinitions())
                .build();
    }

    @NotNull
    private JsonDefinitionsType actionTypeDefinitions() {
        return JsonDefinitionsType.builder()
                .comment(WikiConstants.WIKI_CONFIG_URL)
                .add("actionName", actionNameType())
                .add("action", actionType())
                .add("actions", actionsType())
                .build();
    }

    @NotNull
    private JsonPropertiesSchemaTypeDefinition actionType() {
        return JsonPropertiesSchemaTypeDefinition.builder()
                .addRequiredProperty("name", ref("#/definitions/actionTypes/definitions/actionName"))
                .addRequiredProperty("rules", JsonArraySchemaTypeDefinition.builder()
                        .itemCounts(1, null)
                        .description("The array of rules representing the sequentially triggered steps of the transformation action.")
                        .items(ref(this.jsonRuleTypeDefinitionRegistry.toTypeReference(null, null)))
                        .disallowAdditionalItems()
                        .build())
                .disallowAdditionalProperties()
                .build();
    }

    @NotNull
    private JsonArraySchemaTypeDefinition actionsType() {
        return JsonArraySchemaTypeDefinition.builder()
                .comment(WikiConstants.WIKI_CONFIG_URL)
                .description("The array of transformation action we will be able to select from at startup.")
                .items(ref("#/definitions/actionTypes/definitions/action"))
                .itemCounts(1, null)
                .disallowAdditionalItems()
                .build();
    }

    @NotNull
    private JsonCompositionSchemaTypeDefinitionImpl actionNameType() {
        return JsonCompositionSchemaTypeDefinitionImpl.builder()
                .allOf(ImmutableList.<JsonSchemaObject>builder()
                        .add(ref("#/definitions/commonTypes/definitions/name"))
                        .add(description("The name of the action (rule list) to allow selecting it when we want to start transformation."))
                        .build())
                .build();
    }

    /**
     * Sets the collection of automatically registered rules.
     *
     * @param autoRegisterRules the collection
     */
    public void setAutoRegisterRules(
            @NotNull final List<Class<? extends JsonRule>> autoRegisterRules) {
        autoRegisterRules.forEach(jsonRuleTypeDefinitionRegistry::registerType);
    }

    /**
     * Sets the collection of automatically registered suppliers.
     *
     * @param autoRegisterSuppliers the collection
     */
    public void setAutoRegisterSuppliers(
            @NotNull final List<Class<? extends Supplier<?>>> autoRegisterSuppliers) {
        autoRegisterSuppliers.forEach(supplierTypeDefinitionRegistry::registerType);
    }

    /**
     * Sets the collection of automatically registered functions.
     *
     * @param autoRegisterFunctions the collection
     */
    public void setAutoRegisterFunctions(
            @NotNull final List<Class<? extends Function<?, ?>>> autoRegisterFunctions) {
        autoRegisterFunctions.forEach(functionTypeDefinitionRegistry::registerType);
    }

    /**
     * Sets the collection of automatically registered predicates.
     *
     * @param autoRegisterPredicates the collection
     */
    public void setAutoRegisterPredicates(
            @NotNull final List<Class<? extends Predicate<Object>>> autoRegisterPredicates) {
        autoRegisterPredicates.forEach(predicateTypeDefinitionRegistry::registerType);
    }
}
