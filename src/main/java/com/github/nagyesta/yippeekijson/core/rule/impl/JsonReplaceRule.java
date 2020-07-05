package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.predicate.AnyStringPredicate;
import com.github.nagyesta.yippeekijson.core.rule.AbstractJsonRule;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.*;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A simple rule to replace {@link String} valued fields at a {@link JsonPath}, matching a {@link Predicate}.
 */
@Slf4j
public final class JsonReplaceRule extends AbstractJsonRule {

    static final String RULE_NAME = "replace";
    static final String PARAM_PREDICATE = "predicate";
    static final String PARAM_STRING_FUNCTION = "stringFunction";

    private final Predicate<Object> predicate;
    private final Function<String, String> stringFunction;

    @SuppressWarnings("DefaultAnnotationParam")
    @SchemaDefinition(
            properties = @PropertyDefinitions({
                    @PropertyDefinition(name = PARAM_PREDICATE, required = false,
                            type = @TypeDefinition(itemType = Predicate.class, itemTypeParams = String.class),
                            docs = "Adds an opportunity to filter by value before we apply the replace `stringFunction`."),
                    @PropertyDefinition(name = PARAM_STRING_FUNCTION,
                            type = @TypeDefinition(itemType = Function.class, itemTypeParams = {String.class, String.class}),
                            docs = "Defines how the existing value needs to change during the operation.")
            }),
            sinceVersion = WikiConstants.VERSION_1_0_0,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_RULES, section = "Replace"),
            description = {
                    "This rule is similar to the rename rule, but instead of changing the key of a node, it operates on",
                    "the value of the String nodes matching the path parameter."
            },
            example = @Example(
                    in = "/examples/json/account_replace_in.json",
                    out = "/examples/json/account_replace_out.json",
                    yml = "/examples/yml/replace.yml"
            )
    )
    @NamedRule(RULE_NAME)
    public JsonReplaceRule(@NotNull final FunctionRegistry functionRegistry,
                           @NotNull final RawJsonRule jsonRule) {
        super(jsonRule.getOrder(), JsonPath.compile(jsonRule.getPath()));
        this.predicate = functionRegistry.lookupPredicate(jsonRule.configParamMap(PARAM_PREDICATE), new AnyStringPredicate());
        this.stringFunction = functionRegistry.lookupFunction(jsonRule.configParamMap(PARAM_STRING_FUNCTION));
    }

    @Override
    public void accept(@NotNull final DocumentContext documentContext) {
        documentContext.map(getJsonPath(), (currentValue, configuration) -> {
            if (currentValue instanceof String) {
                if (predicate.test(currentValue)) {
                    return stringFunction.apply((String) currentValue);
                } else {
                    log.info(String.format("Object at jsonPath: \"%s\", did not match predicate. Ignoring.",
                            getJsonPath().getPath()));
                }
            } else {
                log.error(String.format("Attempted String replace on jsonPath: \"%s\", found value: \"%s\". Ignoring.",
                        getJsonPath().getPath(), currentValue));
            }
            return currentValue;
        });
    }

    @Override
    public String toString() {
        return "JsonRule['" + RULE_NAME + "']"
                + "\n\t\torder: " + getOrder()
                + "\n\t\tpath: '" + getJsonPath().getPath() + "'"
                + "\n\t\tpredicate: " + predicate
                + "\n\t\tfunction: " + stringFunction
                + "\n";
    }

}
