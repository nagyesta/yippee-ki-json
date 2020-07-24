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
 * Defines a simple rule adding a given value under a given key of the selected {@link JsonPath}.
 */
@Slf4j
public final class JsonAddRule extends AbstractJsonRule {

    static final String RULE_NAME = "add";
    static final String PARAM_KEY = "key";
    static final String PARAM_VALUE = "value";

    private final Supplier<String> keySupplier;
    private final Supplier<?> valueSupplier;

    @SuppressWarnings("DefaultAnnotationParam")
    @SchemaDefinition(
            properties = @PropertyDefinitions({
                    @PropertyDefinition(name = PARAM_KEY,
                            type = @TypeDefinition(itemType = Supplier.class, itemTypeParams = String.class),
                            docs = "The supplier that will define the key of the new node."),
                    @PropertyDefinition(name = PARAM_VALUE,
                            type = @TypeDefinition(itemType = Supplier.class, itemTypeParams = Object.class),
                            docs = "The supplier that will define the value assigned to the new node identified by the key.")
            }),
            sinceVersion = WikiConstants.VERSION_1_0_0,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_RULES, section = "Add"),
            description = {
                    "This rule adds a new key under the JSON Node matching the JSON Path of the rule.",
                    "Therefore, it is necessary to define the JSON Path in a way that is locating an",
                    "object typed node. The key of the new child is provided by the key Supplier, the",
                    "value assigned to it is supplied by the value Supplier."
            },
            example = @Example(
                    in = "/examples/json/simple-accounts_in.json",
                    out = "/examples/json/add-string-json_out.json",
                    yml = "/examples/yml/add-string-json.yml")
    )
    @NamedRule(RULE_NAME)
    public JsonAddRule(@NotNull final FunctionRegistry functionRegistry,
                       @NotNull final RawJsonRule jsonRule) {
        super(jsonRule.getOrder(), JsonPath.compile(jsonRule.getPath()));
        this.keySupplier = functionRegistry.lookupSupplier(jsonRule.configParamMap(PARAM_KEY));
        this.valueSupplier = functionRegistry.lookupSupplier(jsonRule.configParamMap(PARAM_VALUE));
    }

    @Override
    public void accept(@NotNull final DocumentContext documentContext) {
        final String key = keySupplier.get();
        final Object value = valueSupplier.get();
        log.info(String.format("Adding object at jsonPath: \"%s\". Key: \"%s\"", getJsonPath().getPath(), key));
        documentContext.put(getJsonPath(), key, value);
    }

    @Override
    public String toString() {
        return "JsonRule['" + RULE_NAME + "']"
                + "\n\t\torder: " + getOrder()
                + "\n\t\tpath: '" + getJsonPath().getPath() + "'"
                + "\n\t\tkeySupplier: " + keySupplier
                + "\n\t\tvalueSupplier: " + valueSupplier
                + "\n";
    }
}
