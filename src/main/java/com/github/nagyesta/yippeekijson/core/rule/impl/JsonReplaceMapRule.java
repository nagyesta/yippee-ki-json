package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.impl.helper.JsonMapRuleSupport;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

/**
 * A simple rule to replace {@link Map} valued fields at a {@link com.jayway.jsonpath.JsonPath}, matching a
 * {@link java.util.function.Predicate}.
 */
@Slf4j
public final class JsonReplaceMapRule extends JsonMapRuleSupport {

    static final String PARAM_PREDICATE = "predicate";
    static final String RULE_NAME = "replaceMap";
    static final String PARAM_MAP_FUNCTION = "mapFunction";

    private final Function<Map<String, Object>, Map<String, Object>> mapFunction;

    @NamedRule(RULE_NAME)
    public JsonReplaceMapRule(@NotNull final FunctionRegistry functionRegistry,
                              @NotNull final JsonMapper jsonMapper,
                              @NotNull final RawJsonRule jsonRule) {
        super(functionRegistry, jsonMapper, jsonRule, log, PARAM_PREDICATE);
        this.mapFunction = functionRegistry.lookupFunction(jsonRule.configParamMap(PARAM_MAP_FUNCTION));
    }

    protected Map<String, Object> applyChanges(final Map<String, Object> map) {
        return this.mapFunction.apply(map);
    }

    @Override
    public String toString() {
        return "JsonRule['" + RULE_NAME + "']"
                + "\n\t\torder: " + getOrder()
                + "\n\t\tpath: '" + getJsonPath().getPath() + "'"
                + "\n\t\tpredicate: " + predicate()
                + "\n\t\tfunction: " + mapFunction
                + "\n";
    }

}
