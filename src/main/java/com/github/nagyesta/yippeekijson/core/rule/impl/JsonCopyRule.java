package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.AbstractJsonRule;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.*;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Defines a simple rule copy the single node identified by the {@link JsonPath} to the destination path(s) using a given key.
 */
@Slf4j
public final class JsonCopyRule extends AbstractJsonRule {

    static final String RULE_NAME = "copy";
    static final String PARAM_TO = "to";
    static final String PARAM_TO_VALUE = "value";
    static final String PARAM_KEY = "key";

    private final JsonPath destination;
    private final Supplier<String> keySupplier;

    @SuppressWarnings("DefaultAnnotationParam")
    @SchemaDefinition(
            properties = @PropertyDefinitions({
                    @PropertyDefinition(name = PARAM_KEY,
                            type = @TypeDefinition(itemType = Supplier.class, itemTypeParams = String.class),
                            docs = "The supplier that will define the key we want to copy."),
                    @PropertyDefinition(name = {PARAM_TO, PARAM_TO_VALUE},
                            type = @TypeDefinition(itemType = JsonPath.class),
                            docs = "The destination path where this rule will attempt to create the copied node.")
            }),
            sinceVersion = WikiConstants.VERSION_1_0_0,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_RULES, section = "Copy"),
            description = {
                    "This rule identifies a single object using the JSON Path we provide in the \"path\" parameter and",
                    "copies it as a child under all of the nodes matching the \"to\" JSON Path parameter with the new",
                    "key named as the \"key\" Supplier defines."
            },
            example = @Example(
                    in = "/examples/json/simple-accounts_in.json",
                    out = "/examples/json/copy-name_out.json",
                    yml = "/examples/yml/copy-json.yml"
            )
    )
    @NamedRule(RULE_NAME)
    public JsonCopyRule(@NotNull final FunctionRegistry functionRegistry,
                        @NotNull final RawJsonRule jsonRule) {
        super(jsonRule.getOrder(), JsonPath.compile(jsonRule.getPath()));
        this.destination = JsonPath.compile(jsonRule.configParamMap(PARAM_TO).get(PARAM_TO_VALUE).asString());
        this.keySupplier = functionRegistry.lookupSupplier(jsonRule.configParamMap(PARAM_KEY));
    }

    @Override
    public void accept(@NotNull final DocumentContext documentContext) {
        if (!getJsonPath().isDefinite()) {
            log.error(String.format("Copy source jsonPath: \"%s\" is not definite. Ignoring.", getJsonPath().getPath()));
            return;
        }
        final String key = keySupplier.get();
        final Object value = documentContext.read(getJsonPath());
        log.info(String.format("Copy object from: \"%s\" to: \"%s\". Key: \"%s\"", getJsonPath().getPath(), destination.getPath(), key));
        documentContext.put(destination, key, value);
    }

    @Override
    public String toString() {
        return "JsonRule['" + RULE_NAME + "']"
                + "\n\t\torder: " + getOrder()
                + "\n\t\tpath: '" + getJsonPath().getPath() + "'"
                + "\n\t\tdestination: '" + destination.getPath() + "'"
                + "\n\t\tkeySupplier: " + keySupplier
                + "\n";
    }
}

