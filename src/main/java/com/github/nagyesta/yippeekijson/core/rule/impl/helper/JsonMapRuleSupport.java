package com.github.nagyesta.yippeekijson.core.rule.impl.helper;

import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.predicate.NotNullPredicate;
import com.github.nagyesta.yippeekijson.core.rule.AbstractJsonRule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.mapper.MappingException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Rule allowing easier processing of Map types.
 */
public abstract class JsonMapRuleSupport extends AbstractJsonRule {

    private static final String ALL_CHILDREN_OF_ROOT = "$.[*]";

    private final Predicate<Object> predicate;
    private final JsonMapper jsonMapper;
    private final Logger log;

    public JsonMapRuleSupport(@NotNull final FunctionRegistry functionRegistry,
                              @NotNull final JsonMapper jsonMapper,
                              @NotNull final RawJsonRule jsonRule,
                              @NotNull final Logger log,
                              @NotNull final String predicateName) {
        super(jsonRule.getOrder(), JsonPath.compile(jsonRule.getPath()));
        this.log = log;
        this.jsonMapper = jsonMapper;
        if (jsonRule.getParams().containsKey(predicateName)) {
            this.predicate = functionRegistry.lookupPredicate(jsonRule.configParamMap(predicateName));
        } else {
            this.predicate = new NotNullPredicate();
        }
    }

    @Override
    public void accept(@NotNull final DocumentContext documentContext) {
        if (isRoot()) {
            log.info("Map rule used on root node, replacing all children manually.");
            final Object currentValue = documentContext.read(getJsonPath());
            final Map<String, Object> result = this.doTransformMap(currentValue);
            documentContext.delete(ALL_CHILDREN_OF_ROOT);
            result.forEach((k, v) -> documentContext.put(getJsonPath(), k, v));
        } else {
            try {
                documentContext.map(getJsonPath(), (currentValue, configuration) -> doTransformMap(currentValue));
            } catch (final PathNotFoundException e) {
                log.info("Path not found {}, ignoring.", getJsonPath().getPath());
            }
        }
    }

    protected abstract Map<String, Object> applyChanges(Map<String, Object> currentMap);

    /**
     * Returns the predicate used by this rule.
     *
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
