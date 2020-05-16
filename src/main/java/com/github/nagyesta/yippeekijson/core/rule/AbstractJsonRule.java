package com.github.nagyesta.yippeekijson.core.rule;

import com.jayway.jsonpath.JsonPath;
import org.springframework.util.Assert;

public abstract class AbstractJsonRule implements JsonRule {

    private final int order;
    private final JsonPath jsonPath;

    protected AbstractJsonRule(int order, JsonPath jsonPath) {
        Assert.notNull(jsonPath, "jsonPath cannot be null.");
        this.order = order;
        this.jsonPath = jsonPath;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public JsonPath getJsonPath() {
        return jsonPath;
    }
}
