package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.function.AnyStringPredicate;
import com.github.nagyesta.yippeekijson.core.rule.AbstractJsonRule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A simple rule to replace {@link String} valued fields at a {@link JsonPath}, matching a {@link Predicate}.
 */
@Slf4j
public final class JsonReplaceRule extends AbstractJsonRule {

    private static final String RULE_NAME = "replace";
    private final Predicate<String> predicate;
    private final Function<String, String> stringFunction;

    public JsonReplaceRule(final int order, final JsonPath jsonPath, final Predicate<String> predicate,
                           final Function<String, String> stringFunction) {
        super(order, jsonPath);
        Assert.notNull(predicate, "predicate cannot be null.");
        Assert.notNull(stringFunction, "stringFunction cannot be null.");
        this.predicate = predicate;
        this.stringFunction = stringFunction;
    }

    @NamedRule(RULE_NAME)
    @SuppressWarnings("checkstyle:AvoidInlineConditionals")
    public JsonReplaceRule(final FunctionRegistry functionRegistry, final RawJsonRule jsonRule) {
        this(jsonRule.getOrder(), JsonPath.compile(jsonRule.getPath()),
                jsonRule.getParams().containsKey("predicate")
                        ? functionRegistry.lookupPredicate(jsonRule.getParams().get("predicate"))
                        : new AnyStringPredicate(),
                functionRegistry.lookupFunction(jsonRule.getParams().get("stringFunction")));
    }

    @Override
    public void accept(final DocumentContext documentContext) {
        documentContext.map(getJsonPath(), (currentValue, configuration) -> {
            if (currentValue instanceof String) {
                if (predicate.test((String) currentValue)) {
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
