package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.AbstractJsonRule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.function.Supplier;

/**
 * Defines a simple rule adding a given value under a given key of the selected {@link JsonPath}.
 */
@Slf4j
public final class JsonRenameRule extends AbstractJsonRule {

    private static final String RULE_NAME = "rename";
    private final Supplier<String> oldKeySupplier;
    private final Supplier<String> newKeySupplier;

    public JsonRenameRule(final int order, final JsonPath jsonPath, final Supplier<String> oldKeySupplier,
                          final Supplier<String> newKeySupplier) {
        super(order, jsonPath);
        Assert.notNull(oldKeySupplier, "oldKeySupplier cannot be null.");
        Assert.notNull(newKeySupplier, "newKeySupplier cannot be null.");

        this.oldKeySupplier = oldKeySupplier;
        this.newKeySupplier = newKeySupplier;
    }

    @NamedRule(RULE_NAME)
    public JsonRenameRule(final FunctionRegistry functionRegistry, final RawJsonRule jsonRule) {
        this(jsonRule.getOrder(), JsonPath.compile(jsonRule.getPath()),
                functionRegistry.lookupSupplier(jsonRule.getParams().get("oldKey")),
                functionRegistry.lookupSupplier(jsonRule.getParams().get("newKey")));
    }

    @Override
    public void accept(final DocumentContext documentContext) {
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
