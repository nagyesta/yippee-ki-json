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
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.nagyesta.yippeekijson.core.rule.impl.JsonAddRule.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@LaunchAbortArmed
class JsonAddRuleTest {

    private static final String A_C_FOO = "{\"a\":{\"c\":\"foo\"}}";
    private static final String A_C_FOO_B_C_BAR = "{\"a\":{\"c\":\"foo\"},\"b\":{\"c\":\"bar\"}}";
    private static final String A_C_FOO_E_D_BAZ_B_C_BAR = "{\"a\":{\"c\":\"foo\",\"e\":{\"d\":\"baz\"}},\"b\":{\"c\":\"bar\"}}";
    private static final String NODE_NAMED_A_ANYWHERE = "$..a";
    private static final String B = "b";
    private static final String E = "e";
    private static final String ROOT = "$";
    private static final Supplier<Object> MAP_SUPPLIER_C_BAR = () -> Map.of("c", "bar");
    private static final Supplier<Object> MAP_SUPPLIER_D_BAZ = () -> Map.of("d", "baz");
    private static final String KEY = "key";
    private static final Map<String, Object> KEY_MAP = Map.of(KEY, KEY);
    private static final String VALUE = "value";
    private static final Map<String, Object> VALUE_MAP = Map.of(VALUE, VALUE);

    private static Stream<Arguments> validInputProvider() {
        return Stream.of(
                Arguments.of(A_C_FOO_B_C_BAR, NODE_NAMED_A_ANYWHERE, E, MAP_SUPPLIER_D_BAZ, A_C_FOO_E_D_BAZ_B_C_BAR),
                Arguments.of(A_C_FOO, ROOT, B, MAP_SUPPLIER_C_BAR, A_C_FOO_B_C_BAR)
        );
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testAcceptShouldAddNewSnippet(final String input, final String path, final String key,
                                       final Supplier<Object> insert, final String expected) {
        //given
        final JsonMapper jsonMapper = new JsonMapperImpl();
        final DocumentContext document = JsonPath.parse(input, jsonMapper.parserConfiguration());
        RawJsonRule raw = RawJsonRule.builder()
                .path(path)
                .name(RULE_NAME)
                .order(0)
                .putParams(Map.of(PARAM_KEY, KEY_MAP, PARAM_VALUE, VALUE_MAP))
                .build();

        final FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        when(functionRegistry.lookupSupplier(argThat(argument -> argument == null || argument.containsKey(KEY)))).thenReturn(() -> key);
        when(functionRegistry.lookupSupplier(argThat(argument -> argument == null || argument.containsKey(VALUE)))).thenReturn(insert);

        final JsonAddRule rule = new JsonAddRule(functionRegistry, raw);

        //when
        rule.accept(document);

        //then
        Assertions.assertEquals(expected, document.jsonString());
    }
}
