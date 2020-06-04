package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.predicate.NotNullPredicate;
import com.github.nagyesta.yippeekijson.core.rule.AbstractJsonRule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.mapper.MappingException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Map;
import java.util.function.Predicate;

public abstract class JsonMapRule extends AbstractJsonRule {
    static final String PARAM_PREDICATE = "predicate";
    private static final String ALL_CHILDREN_OF_ROOT = "$.[*]";

    private final Predicate<Object> predicate;
    private final JsonMapper jsonMapper;
    private final Logger log;

    public JsonMapRule(@NotNull final FunctionRegistry functionRegistry,
                       @NotNull final RawJsonRule jsonRule,
                       @NotNull final Logger log) {
        super(jsonRule.getOrder(), JsonPath.compile(jsonRule.getPath()));
        this.log = log;
        this.jsonMapper = functionRegistry.jsonMapper();
        if (jsonRule.getParams().containsKey(PARAM_PREDICATE)) {
            this.predicate = functionRegistry.lookupPredicate(jsonRule.configParamMap(PARAM_PREDICATE));
        } else {
            this.predicate = new NotNullPredicate();
        }
    }

    @Override
    public void accept(@NotNull final DocumentContext documentContext) {
        if (isRoot()) {
            log.info("Map rule used on root node, replacing ann children manually.");
            final Object currentValue = documentContext.read(getJsonPath());
            final Map<String, Object> result = this.doTransformMap(currentValue);
            documentContext.delete(ALL_CHILDREN_OF_ROOT);
            result.forEach((k, v) -> documentContext.put(getJsonPath(), k, v));
        } else {
            documentContext.map(getJsonPath(), (currentValue, configuration) -> doTransformMap(currentValue));
        }
    }

    protected abstract Map<String, Object> applyChanges(Map<String, Object> currentMap);

    /**
     * Returns the predicate used by this rule.
     * @return the predicate used for root object evaluation.
     */
    protected Predicate<Object> predicate() {
        return predicate;
    }

    private Map<String, Object> doTransformMap(final Object currentValue) {
        try {
            final Map<String, Object> map = jsonMapper.mapTo(currentValue, JsonMapper.MapTypeRef.INSTANCE);
            Map<String, Object> result = map;
            if (predicate.test(map)) {
                result = applyChanges(map);
            }
            return result;
        } catch (final MappingException e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private boolean isRoot() {
        return getJsonPath().isDefinite() && getJsonPath().getPath().equals("$");
    }
}
