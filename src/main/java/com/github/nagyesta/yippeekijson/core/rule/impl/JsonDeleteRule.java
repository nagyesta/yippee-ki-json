package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.AbstractJsonRule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

/**
 * Defines a simple rule deleting nodes matching the selected {@link JsonPath}.
 */
@Slf4j
public final class JsonDeleteRule extends AbstractJsonRule {

    private static final String RULE_NAME = "delete";

    @TestOnly
    protected JsonDeleteRule(@NotNull final Integer order,
                             @NotNull final JsonPath jsonPath) {
        super(order, jsonPath);
    }

    @NamedRule(RULE_NAME)
    public JsonDeleteRule(@SuppressWarnings("unused") @NotNull final FunctionRegistry functionRegistry,
                          @NotNull final RawJsonRule jsonRule) {
        super(jsonRule.getOrder(), JsonPath.compile(jsonRule.getPath()));
    }

    @Override
    public void accept(@NotNull final DocumentContext documentContext) {
        log.info(String.format("Deleting object at jsonPath: \"%s\".", getJsonPath().getPath()));
        documentContext.delete(getJsonPath());
    }

    @Override
    public String toString() {
        return "JsonRule['" + RULE_NAME + "']"
                + "\n\t\torder: " + getOrder()
                + "\n\t\tpath: '" + getJsonPath().getPath() + "'\n";
    }

}
