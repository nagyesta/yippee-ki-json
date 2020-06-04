package com.github.nagyesta.yippeekijson.core.rule.impl;

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
import java.util.stream.Stream;

import static com.github.nagyesta.yippeekijson.core.rule.impl.JsonCopyRule.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JsonCopyRuleTest {

    private static final String A_C_FOO = "{\"a\":{\"c\":\"foo\"}}";
    private static final String A_C_FOO_B_C_BAR = "{\"a\":{\"c\":\"foo\"},\"b\":{\"c\":\"bar\"}}";
    private static final String A_C_FOO_B_C_FOO = "{\"a\":{\"c\":\"foo\"},\"b\":{\"c\":\"foo\"}}";
    private static final String A_C_FOO_B_FOO_B_C_BAR_B_FOO = "{\"a\":{\"c\":\"foo\",\"b\":\"foo\"},\"b\":{\"c\":\"bar\",\"b\":\"foo\"}}";
    private static final String NODE_NAMED_A = "$.a";
    private static final String NODE_NAMED_A_C = "$.a.c";
    private static final String NODES_IN_THE_ROOT = "$.*";
    private static final String B = "b";
    private static final String ROOT = "$";
    private static final String KEY = "key";
    private static final Map<String, Object> KEY_MAP = Map.of(KEY, KEY);

    private static Stream<Arguments> validInputProvider() {
        return Stream.of(
                Arguments.of(A_C_FOO, NODE_NAMED_A, ROOT, B, A_C_FOO_B_C_FOO),
                Arguments.of(A_C_FOO_B_C_BAR, NODE_NAMED_A_C, NODES_IN_THE_ROOT, B, A_C_FOO_B_FOO_B_C_BAR_B_FOO),
                Arguments.of(A_C_FOO_B_C_BAR, NODES_IN_THE_ROOT, NODE_NAMED_A, B, A_C_FOO_B_C_BAR) //skip if source is not definitive
        );
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testAcceptShouldCopyObject(final String input, final String from, final String to, final String key, final String expected) {
        //given
        final JsonMapper jsonMapper = new JsonMapperImpl();
        final DocumentContext document = JsonPath.parse(input, jsonMapper.parserConfiguration());
        RawJsonRule raw = RawJsonRule.builder()
                .path(from)
                .name(RULE_NAME)
                .order(0)
                .putParams(Map.of(PARAM_KEY, KEY_MAP, PARAM_TO, Map.of(PARAM_TO_VALUE, to)))
                .build();

        final FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        when(functionRegistry.lookupSupplier(argThat(argument -> argument == null || argument.containsKey(KEY)))).thenReturn(() -> key);
        final JsonCopyRule rule = new JsonCopyRule(functionRegistry, raw);

        //when
        rule.accept(document);

        //then
        Assertions.assertEquals(expected, document.jsonString());
    }
}
