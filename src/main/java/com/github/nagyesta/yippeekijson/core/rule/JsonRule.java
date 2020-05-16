package com.github.nagyesta.yippeekijson.core.rule;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.springframework.util.comparator.Comparators;

import java.util.function.Consumer;

public interface JsonRule extends Consumer<DocumentContext>, Comparable<JsonRule> {

    int getOrder();

    JsonPath getJsonPath();

    @Override
    default int compareTo(JsonRule o) {
        return Comparators.comparable().compare(getOrder(), o.getOrder());
    }
}
