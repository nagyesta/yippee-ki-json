package com.github.nagyesta.yippeekijson.core.rule;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AbstractJsonRuleTest {

    private static final Function<Integer, JsonRule> ORDER_TO_RULE_FUNCTION = o -> new AbstractJsonRule(o, JsonPath.compile("$")) {
        @Override
        public void accept(DocumentContext documentContext) {
            //noop
        }
    };

    private static Stream<Arguments> ordersProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(Stream.of(3, 2, 1, 4, 5).map(ORDER_TO_RULE_FUNCTION).collect(Collectors.toList())))
                .add(Arguments.of(Stream.of(1).map(ORDER_TO_RULE_FUNCTION).collect(Collectors.toList())))
                .add(Arguments.of(Stream.of(1, 2, 3).map(ORDER_TO_RULE_FUNCTION).collect(Collectors.toList())))
                .add(Arguments.of(Stream.of(34, 32, 12).map(ORDER_TO_RULE_FUNCTION).collect(Collectors.toList())))
                .build();
    }

    @ParameterizedTest
    @MethodSource("ordersProvider")
    void testOrderingShouldSortRulesByOrderAscending(List<JsonRule> rules) {
        //given

        //when
        Collections.sort(rules);

        //then
        int lastOrder = Integer.MIN_VALUE;
        for (JsonRule r : rules) {
            Assertions.assertTrue(lastOrder < r.getOrder());
            lastOrder = r.getOrder();
        }
    }
}