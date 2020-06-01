package com.github.nagyesta.yippeekijson.core.rule;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.comparator.Comparators;

import java.util.function.Consumer;

/**
 * Defines the behavior of JSON transformation rules within a {@link com.github.nagyesta.yippeekijson.core.config.entities.JsonAction}.
 */
public interface JsonRule extends Consumer<DocumentContext>, Comparable<JsonRule> {

    /**
     * The order used for sorting rules put into the same collection.
     *
     * @return the order of the current rule.
     */
    int getOrder();

    /**
     * The JSON Path this rule is operating on.
     *
     * @return the path
     */
    @NotNull JsonPath getJsonPath();

    @Override
    default int compareTo(final JsonRule o) {
        return Comparators.comparable().compare(getOrder(), o.getOrder());
    }
}
