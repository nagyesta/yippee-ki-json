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

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.nagyesta.yippeekijson.core.rule.impl.JsonReplaceRule.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@LaunchAbortArmed
class JsonReplaceRuleTest {

    private static final Predicate<String> NON_NULL = Objects::nonNull;
    private static final Predicate<String> STARTS_WITH_F = text -> text.startsWith("f");
    private static final Predicate<String> STARTS_WITH_B = text -> text.startsWith("B");
    private static final Function<String, String> UPPER_CASE = String::toUpperCase;
    private static final Function<String, String> LOWER_CASE = String::toLowerCase;
    private static final String A_C_FOO_B_C_BAR = "{\"a\":{\"c\":\"foo\"},\"b\":{\"c\":\"bar\"}}";
    private static final String A_C_UPPER_FOO_B_C_BAR = "{\"a\":{\"c\":\"FOO\"},\"b\":{\"c\":\"bar\"}}";
    private static final String A_C_FOO_B_C_UPPER_BAR = "{\"a\":{\"c\":\"foo\"},\"b\":{\"c\":\"BAR\"}}";
    private static final String NODE_NAMED_C_ANYWHERE = "$..c";
    private static final String NODE_NAMED_A_ANYWHERE = "$..a";

    private static Stream<Arguments> validInputProvider() {
        return Stream.of(
                Arguments.of(A_C_FOO_B_C_BAR, NODE_NAMED_C_ANYWHERE, STARTS_WITH_F, UPPER_CASE, A_C_UPPER_FOO_B_C_BAR),
                Arguments.of(A_C_FOO_B_C_UPPER_BAR, NODE_NAMED_C_ANYWHERE, STARTS_WITH_B, LOWER_CASE, A_C_FOO_B_C_BAR),
                Arguments.of(A_C_FOO_B_C_BAR, NODE_NAMED_C_ANYWHERE, STARTS_WITH_B, UPPER_CASE, A_C_FOO_B_C_BAR),
                Arguments.of(A_C_FOO_B_C_BAR, NODE_NAMED_A_ANYWHERE, NON_NULL, UPPER_CASE, A_C_FOO_B_C_BAR)
        );
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testAcceptShouldReplaceMatchingStringsOnly(final String input, final String path, final Predicate<Object> matches,
                                                    final Function<String, String> replace, final String expected) {
        //given
        final JsonMapper jsonMapper = new JsonMapperImpl();
        final DocumentContext document = JsonPath.parse(input, jsonMapper.parserConfiguration());
        RawJsonRule raw = RawJsonRule.builder()
                .path(path)
                .name(RULE_NAME)
                .order(0)
                .putParams(Map.of(PARAM_STRING_FUNCTION, Map.of(), PARAM_PREDICATE, Map.of()))
                .build();

        final FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        when(functionRegistry.lookupFunction(anyMap())).thenReturn(s -> replace.apply((String) s));
        when(functionRegistry.lookupPredicate(anyMap(), any(Predicate.class))).thenReturn(matches);

        final JsonReplaceRule rule = new JsonReplaceRule(functionRegistry, raw);

        //when
        rule.accept(document);

        //then
        Assertions.assertEquals(expected, document.jsonString());
    }
}
