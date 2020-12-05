package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.JsonMapperImpl;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.nagyesta.yippeekijson.core.rule.impl.JsonCalculateRule.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@LaunchAbortArmed
class JsonCalculateRuleTest {

    private static final int INT_20 = 20;
    private static final Predicate<BigDecimal> MORE_THAN_20 = number -> number.longValue() > INT_20;
    private static final Predicate<BigDecimal> LESS_THAN_20 = number -> number.longValue() < INT_20;
    private static final Function<BigDecimal, BigDecimal> DOUBLE = number -> number.multiply(BigDecimal.valueOf(2))
            .setScale(0, RoundingMode.HALF_EVEN);
    private static final int INT_32 = 32;
    private static final Function<BigDecimal, BigDecimal> ADD_32 = number -> number.add(BigDecimal.valueOf(INT_32));
    private static final String A_C_21_B_C_21 = "{\"a\":{\"c\":21.0},\"b\":{\"c\":21}}";
    private static final String A_C_42_B_C_42 = "{\"a\":{\"c\":42},\"b\":{\"c\":42}}";
    private static final String A_C_10_B_C_42 = "{\"a\":{\"c\":\"10\"},\"b\":{\"c\":42}}";
    private static final String NODE_NAMED_C_ANYWHERE = "$..c";
    private static final String NODE_NAMED_A_ANYWHERE = "$..a";

    private static Stream<Arguments> validInputProvider() {
        return Stream.of(
                Arguments.of(A_C_21_B_C_21, NODE_NAMED_C_ANYWHERE, MORE_THAN_20, DOUBLE, A_C_42_B_C_42),
                Arguments.of(A_C_10_B_C_42, NODE_NAMED_C_ANYWHERE, LESS_THAN_20, ADD_32, A_C_42_B_C_42),
                Arguments.of(A_C_21_B_C_21, NODE_NAMED_C_ANYWHERE, LESS_THAN_20, DOUBLE, A_C_21_B_C_21),
                Arguments.of(A_C_21_B_C_21, NODE_NAMED_A_ANYWHERE, null, DOUBLE, A_C_21_B_C_21)
        );
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testAcceptShouldReplaceMatchingStringsOnly(final String input, final String path, final Predicate<Object> matches,
                                                    final Function<BigDecimal, BigDecimal> calculate, final String expected) {
        //given
        final JsonMapper jsonMapper = new JsonMapperImpl();
        final DocumentContext document = JsonPath.parse(input, jsonMapper.parserConfiguration());
        final RawJsonRule.RawJsonRuleBuilder builder = RawJsonRule.builder()
                .path(path)
                .name(RULE_NAME)
                .order(0);

        if (matches == null) {
            builder.putParams(Map.of(PARAM_NUMBER_FUNCTION, Map.of()));
        } else {
            builder.putParams(Map.of(PARAM_NUMBER_FUNCTION, Map.of(), PARAM_PREDICATE, Map.of()));
        }
        RawJsonRule raw = builder
                .build();

        final FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        when(functionRegistry.lookupFunction(anyMap())).thenReturn(bd -> calculate.apply((BigDecimal) bd));
        when(functionRegistry.lookupPredicate(anyMap(), any(Predicate.class))).thenReturn(matches);

        final JsonCalculateRule rule = new JsonCalculateRule(functionRegistry, raw);

        //when
        rule.accept(document);

        //then
        Assertions.assertEquals(expected, document.jsonString());
    }
}
