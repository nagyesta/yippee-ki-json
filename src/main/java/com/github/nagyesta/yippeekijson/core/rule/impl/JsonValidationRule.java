package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.AbstractJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.strategy.TransformationControlStrategy;
import com.github.nagyesta.yippeekijson.core.rule.strategy.ViolationStrategy;
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
