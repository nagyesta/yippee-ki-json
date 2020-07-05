package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.AbstractJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.strategy.TransformationControlStrategy;
import com.github.nagyesta.yippeekijson.core.rule.strategy.ViolationStrategy;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.*;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.mapper.MappingException;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Defines a rule validating the nodes selected by the {@link JsonPath}.
 */
@Slf4j
public class JsonValidationRule extends AbstractJsonRule {

    static final String RULE_NAME = "validate";
    static final String PARAM_SCHEMA = "schema";
    static final String PARAM_ON_FAILURE = "onFailure";
    static final String PARAM_ON_FAILURE_TRANSFORMATION = "transformation";
    static final String PARAM_ON_FAILURE_VIOLATION = "violation";
    static final String ROOT_NODE = "$";
    private final Supplier<JsonSchema> schemaSupplier;
    private final TransformationControlStrategy transformationControlStrategy;
    private final ViolationStrategy violationStrategy;

    @SchemaDefinition(
            pathRestriction = @JsonPathRestriction(
                    constant = "$", docs = "Must be the root JSON Path as the validator cannot work on sub-schema."
            ),
            properties = @PropertyDefinitions({
                    @PropertyDefinition(name = PARAM_SCHEMA,
                            type = @TypeDefinition(itemType = Supplier.class, itemTypeParams = JsonSchema.class),
                            docs = "The supplier providing the schema we will use for validation."),
                    @PropertyDefinition(name = {PARAM_ON_FAILURE, PARAM_ON_FAILURE_TRANSFORMATION},
                            type = @TypeDefinition(itemType = TransformationControlStrategy.class),
                            docs = "Defines how the transformation should handle a validation failure."),
                    @PropertyDefinition(name = {PARAM_ON_FAILURE, PARAM_ON_FAILURE_VIOLATION},
                            type = @TypeDefinition(itemType = ViolationStrategy.class),
                            docs = "Defines how the rule should document/report the violations.")
            }),
            sinceVersion = WikiConstants.VERSION_1_2_0,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_RULES, section = "JSON Schema validate"),
            description = {
                    "This rather complex rule validates the document processed by the rule against a schema that is supplied",
                    "by the \"schema\" parameter. In case the validation passed, the rule is considered to be finished and",
                    "nothing else happens. Otherwise the rule will use the defined strategies which are received as parameters",
                    "to handle the failure as requested. The transformation strategy decides whether the transformation should",
                    "be considered as aborted (no output provided), failed (partial results are kept allowing addition of extra",
                    "validation specific information) or successful (basically ignoring the fact that the validation failed).",
                    "The violation strategy defines whether we want to log the violations to the console, add violations and",
                    "some reasoning into the JSON itself or just simply ignore the violations."
            },
            example = @Example(
                    in = "/examples/json/validation-input.json",
                    out = "/examples/json/validation-output.json",
                    yml = "/examples/yml/validation.yml",
                    note = {
                            "The above example shows that the `additionalProperties` node caused itself to be invalid as it was not",
                            "listed in the properties object as an allowed key."
                    }
            )
    )
    @NamedRule(RULE_NAME)
    public JsonValidationRule(@NotNull final FunctionRegistry functionRegistry,
                              @NotNull final RawJsonRule jsonRule) {
        super(jsonRule.getOrder(), JsonPath.compile(ROOT_NODE));
        this.schemaSupplier = functionRegistry.lookupSupplier(jsonRule.configParamMap(PARAM_SCHEMA));
        final Map<String, RawConfigParam> onMap = jsonRule.configParamMap(PARAM_ON_FAILURE);
        Assert.isTrue(onMap.containsKey(PARAM_ON_FAILURE_TRANSFORMATION), "onFailure.transformation value is mandatory");
        this.transformationControlStrategy = toEnum(onMap.get(PARAM_ON_FAILURE_TRANSFORMATION).asString(),
                TransformationControlStrategy.values(), TransformationControlStrategy.class);
        this.violationStrategy = toEnum(onMap.get(PARAM_ON_FAILURE_VIOLATION).asString(),
                ViolationStrategy.values(), ViolationStrategy.class);
    }

    private static <T extends Enum<T>> T toEnum(final String value, final T[] values, final Class<T> clazz) {
        return Arrays.stream(values)
                .filter(c -> c.name().equalsIgnoreCase(value))
                .findFirst().orElseThrow(()
                        -> new IllegalArgumentException("Unknown " + clazz.getSimpleName() + " supplied: " + value));
    }

    @Override
    public void accept(final DocumentContext documentContext) {
        try {
            final JsonNode rootNode = documentContext.read(ROOT_NODE, JsonNode.class);
            final JsonSchema jsonSchema = this.schemaSupplier.get();
            log.info("Validating " + ROOT_NODE);
            final Set<ValidationMessage> violations = jsonSchema.validate(rootNode, rootNode, ROOT_NODE);
            this.violationStrategy.accept(documentContext, violations);
            this.transformationControlStrategy.accept(violations);
        } catch (final MappingException e) {
            log.error("Processing failed: " + e.getMessage(), e);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
