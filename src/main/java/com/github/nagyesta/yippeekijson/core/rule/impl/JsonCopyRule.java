package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.AbstractJsonRule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.function.Supplier;

/**
 * Defines a simple rule copy the single node identified by the {@link JsonPath} to the destination path(s) using a given key.
 */
@Slf4j
public final class JsonCopyRule extends AbstractJsonRule {

    private static final String RULE_NAME = "copy";

    private final JsonPath destination;
    private final Supplier<String> keySupplier;

    public JsonCopyRule(int order, JsonPath jsonPath, JsonPath destination, Supplier<String> keySupplier) {
        super(order, jsonPath);
        Assert.notNull(destination, "destination cannot be null.");
        Assert.notNull(keySupplier, "keySupplier cannot be null.");

        this.destination = destination;
        this.keySupplier = keySupplier;
    }

    @NamedRule(RULE_NAME)
    public JsonCopyRule(FunctionRegistry functionRegistry, RawJsonRule jsonRule) {
        this(jsonRule.getOrder(), JsonPath.compile(jsonRule.getPath()),
                JsonPath.compile(jsonRule.getParams().get("to").get("value")),
                functionRegistry.lookupSupplier(jsonRule.getParams().get("key")));
    }

    @Override
    public void accept(DocumentContext documentContext) {
        if (!getJsonPath().isDefinite()) {
            log.error(String.format("Copy source jsonPath: \"%s\" is not definite. Ignoring.", getJsonPath().getPath()));
            return;
        }
        String key = keySupplier.get();
        Object value = documentContext.read(getJsonPath());
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

