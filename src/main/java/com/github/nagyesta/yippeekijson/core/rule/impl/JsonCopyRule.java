package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.AbstractJsonRule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * Defines a simple rule copy the single node identified by the {@link JsonPath} to the destination path(s) using a given key.
 */
@Slf4j
public final class JsonCopyRule extends AbstractJsonRule {

    private static final String RULE_NAME = "copy";

    private final JsonPath destination;
    private final Supplier<String> keySupplier;

    public JsonCopyRule(final int order, @NonNull final JsonPath jsonPath, @NonNull final JsonPath destination,
                        @NonNull final Supplier<String> keySupplier) {
        super(order, jsonPath);
        this.destination = destination;
        this.keySupplier = keySupplier;
    }

    @NamedRule(RULE_NAME)
    public JsonCopyRule(@NonNull final FunctionRegistry functionRegistry, @NonNull final RawJsonRule jsonRule) {
        this(jsonRule.getOrder(), JsonPath.compile(jsonRule.getPath()),
                JsonPath.compile(jsonRule.getParams().get("to").get("value")),
                functionRegistry.lookupSupplier(jsonRule.getParams().get("key")));
    }

    @Override
    public void accept(final DocumentContext documentContext) {
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

