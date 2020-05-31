package com.github.nagyesta.yippeekijson.core.rule;

import com.jayway.jsonpath.JsonPath;
import lombok.NonNull;

public abstract class AbstractJsonRule implements JsonRule {

    private final int order;
    private final JsonPath jsonPath;

    protected AbstractJsonRule(final int order, @NonNull final JsonPath jsonPath) {
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
