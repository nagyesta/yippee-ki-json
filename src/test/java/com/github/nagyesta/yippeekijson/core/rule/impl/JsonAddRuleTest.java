package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.ParseContextImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

class JsonAddRuleTest {

    private static final String A_C_FOO = "{\"a\":{\"c\":\"foo\"}}";
    private static final String A_C_FOO_B_C_BAR = "{\"a\":{\"c\":\"foo\"},\"b\":{\"c\":\"bar\"}}";
    private static final String A_C_FOO_E_D_BAZ_B_C_BAR = "{\"a\":{\"c\":\"foo\",\"e\":{\"d\":\"baz\"}},\"b\":{\"c\":\"bar\"}}";
    private static final String NODE_NAMED_A_ANYWHERE = "$..a";
    private static final String B = "b";
    private static final String E = "e";
    private static final String ROOT = "$";
    private static final Supplier<String> NULL_SUPPLIER = () -> null;
    private static final Supplier<Object> MAP_SUPPLIER_C_BAR = () -> Map.of("c", "bar");
    private static final Supplier<Object> MAP_SUPPLIER_D_BAZ = () -> Map.of("d", "baz");

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
        final DocumentContext document = new ParseContextImpl().parse(input);
        final JsonAddRule rule = new JsonAddRule(0, JsonPath.compile(path), () -> key, insert);

        //when
        rule.accept(document);

        //then
        Assertions.assertEquals(expected, document.jsonString());
    }
}
