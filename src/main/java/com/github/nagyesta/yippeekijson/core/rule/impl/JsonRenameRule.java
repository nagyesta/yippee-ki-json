package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.AbstractJsonRule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Defines a simple rule adding a given value under a given key of the selected {@link JsonPath}.
 */
@Slf4j
public final class JsonRenameRule extends AbstractJsonRule {

    static final String RULE_NAME = "rename";
    static final String PARAM_OLD_KEY = "oldKey";
    static final String PARAM_NEW_KEY = "newKey";

    private final Supplier<String> oldKeySupplier;
    private final Supplier<String> newKeySupplier;

    @NamedRule(RULE_NAME)
    public JsonRenameRule(@NotNull final FunctionRegistry functionRegistry,
                          @NotNull final RawJsonRule jsonRule) {
        super(jsonRule.getOrder(), JsonPath.compile(jsonRule.getPath()));
        this.oldKeySupplier = functionRegistry.lookupSupplier(jsonRule.configParamMap(PARAM_OLD_KEY));
        this.newKeySupplier = functionRegistry.lookupSupplier(jsonRule.configParamMap(PARAM_NEW_KEY));
    }

    @Override
    public void accept(@NotNull final DocumentContext documentContext) {
        final String oldKeyName = oldKeySupplier.get();
        final String newKeyName = newKeySupplier.get();
        if (oldKeyName.equals(newKeyName)) {
            log.info(String.format("Skipping identical key rename at jsonPath: \"%s\". OldKey: \"%s\", NewKey: \"%s\".",
                    getJsonPath().getPath(), oldKeyName, newKeyName));
            return;
        }
        log.info(String.format("Renaming key at jsonPath: \"%s\". OldKey: \"%s\", NewKey: \"%s\".",
                getJsonPath().getPath(), oldKeyName, newKeyName));
        try {
            documentContext.renameKey(getJsonPath(), oldKeyName, newKeyName);
        } catch (final PathNotFoundException e) {
            log.error(String.format("Failed to rename key at jsonPath: \"%s\": %s", getJsonPath().getPath(), e.getMessage()));
        }
    }

    @Override
    public String toString() {
        return "JsonRule['" + RULE_NAME + "']"
                + "\n\t\torder: " + getOrder()
                + "\n\t\tpath: '" + getJsonPath().getPath() + "'"
                + "\n\t\toldKey: '" + oldKeySupplier.get()
                + "\n\t\tnewKey: " + newKeySupplier.get()
                + "\n";
    }

}
