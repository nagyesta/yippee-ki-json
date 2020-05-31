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
 * Defines a simple rule adding a given value under a given key of the selected {@link JsonPath}.
 */
@Slf4j
public final class JsonAddRule extends AbstractJsonRule {

    private static final String RULE_NAME = "add";

    private final Supplier<String> keySupplier;
    private final Supplier<?> valueSupplier;

    public JsonAddRule(final int order, @NonNull final JsonPath jsonPath, @NonNull final Supplier<String> keySupplier,
                       @NonNull final Supplier<?> valueSupplier) {
        super(order, jsonPath);
        this.keySupplier = keySupplier;
        this.valueSupplier = valueSupplier;
    }

    @NamedRule(RULE_NAME)
    public JsonAddRule(final FunctionRegistry functionRegistry, final RawJsonRule jsonRule) {
        this(jsonRule.getOrder(), JsonPath.compile(jsonRule.getPath()),
                functionRegistry.lookupSupplier(jsonRule.getParams().get("key")),
                functionRegistry.lookupSupplier(jsonRule.getParams().get("value")));
    }

    @Override
    public void accept(final DocumentContext documentContext) {
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
