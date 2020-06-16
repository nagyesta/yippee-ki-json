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

import static com.github.nagyesta.yippeekijson.core.rule.impl.JsonRenameRule.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JsonRenameRuleTest {

    private static final String C = "c";
    private static final String D = "d";
    private static final String A_C_FOO_B_C_BAR = "{\"a\":{\"c\":\"foo\"},\"b\":{\"c\":\"bar\"}}";
    private static final String A_D_FOO_B_D_BAR = "{\"a\":{\"d\":\"foo\"},\"b\":{\"d\":\"bar\"}}";
    private static final String A_C_FOO_B_D_BAR = "{\"a\":{\"c\":\"foo\"},\"b\":{\"d\":\"bar\"}}";
    private static final String ANY_NODE = "$.*";
    private static final String NODE_NAMED_B_ANYWHERE = "$..b";
    private static final String OLD_KEY = "oldKey";
    private static final Map<String, Object> OLD_KEY_MAP = Map.of(OLD_KEY, OLD_KEY);
    private static final String NEW_KEY = "newKey";
    private static final Map<String, Object> NEW_KEY_MAP = Map.of(NEW_KEY, NEW_KEY);

    private static Stream<Arguments> validInputProvider() {
        return Stream.of(
                Arguments.of(A_C_FOO_B_C_BAR, ANY_NODE, C, D, A_D_FOO_B_D_BAR),
                Arguments.of(A_D_FOO_B_D_BAR, ANY_NODE, D, C, A_C_FOO_B_C_BAR),
                Arguments.of(A_C_FOO_B_C_BAR, NODE_NAMED_B_ANYWHERE, C, D, A_C_FOO_B_D_BAR),
                Arguments.of(A_C_FOO_B_C_BAR, ANY_NODE, D, D, A_C_FOO_B_C_BAR), //skip if names are the same
                Arguments.of(A_C_FOO_B_C_BAR, ANY_NODE, D, C, A_C_FOO_B_C_BAR)  //skip if not matched
        );
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testAcceptShouldRenameNodes(final String input, final String path, final String oldKey,
                                     final String newKey, final String expected) {
        //given
        final JsonMapper jsonMapper = new JsonMapperImpl();
        final DocumentContext document = JsonPath.parse(input, jsonMapper.parserConfiguration());
        RawJsonRule raw = RawJsonRule.builder()
                .path(path)
                .name(RULE_NAME)
                .order(0)
                .putParams(Map.of(PARAM_OLD_KEY, OLD_KEY_MAP, PARAM_NEW_KEY, NEW_KEY_MAP))
                .build();

        final FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        when(functionRegistry.lookupSupplier(argThat(argument -> argument == null
                || argument.containsKey(OLD_KEY)))).thenReturn(() -> oldKey);
        when(functionRegistry.lookupSupplier(argThat(argument -> argument == null
                || argument.containsKey(NEW_KEY)))).thenReturn(() -> newKey);

        final JsonRenameRule rule = new JsonRenameRule(functionRegistry, raw);

        //when
        rule.accept(document);

        //then
        Assertions.assertEquals(expected, document.jsonString());
    }
}
