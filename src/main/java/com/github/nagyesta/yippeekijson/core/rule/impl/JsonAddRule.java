package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.AbstractJsonRule;
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
