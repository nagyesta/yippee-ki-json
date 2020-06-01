package com.github.nagyesta.yippeekijson.core.rule;

import com.jayway.jsonpath.JsonPath;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract superclass of {@link JsonRule} implementations.
 */
public abstract class AbstractJsonRule implements JsonRule {

    private final int order;
    private final JsonPath jsonPath;

    protected AbstractJsonRule(@NonNull final Integer order, @NonNull final JsonPath jsonPath) {
        this.order = order;
        this.jsonPath = jsonPath;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public @NotNull JsonPath getJsonPath() {
        return jsonPath;
    }
}
