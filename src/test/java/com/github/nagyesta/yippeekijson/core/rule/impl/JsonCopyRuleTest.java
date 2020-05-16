package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.ParseContextImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

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
    private static final Supplier<String> NULL_SUPPLIER = () -> null;

    private static Stream<Arguments> validInputProvider() {
        return Stream.of(
                Arguments.of(A_C_FOO, NODE_NAMED_A, ROOT, B, A_C_FOO_B_C_FOO),
                Arguments.of(A_C_FOO_B_C_BAR, NODE_NAMED_A_C, NODES_IN_THE_ROOT, B, A_C_FOO_B_FOO_B_C_BAR_B_FOO),
                Arguments.of(A_C_FOO_B_C_BAR, NODES_IN_THE_ROOT, NODE_NAMED_A, B, A_C_FOO_B_C_BAR) //skip if source is not definitive
        );
    }

    private static Stream<Arguments> invalidInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null, null))
                .add(Arguments.of(JsonPath.compile(ROOT), null, null))
                .add(Arguments.of(null, JsonPath.compile(ROOT), null))
                .add(Arguments.of(null, null, NULL_SUPPLIER))
                .add(Arguments.of(JsonPath.compile(ROOT), JsonPath.compile(ROOT), null))
                .add(Arguments.of(JsonPath.compile(ROOT), null, NULL_SUPPLIER))
                .add(Arguments.of(null, JsonPath.compile(ROOT), NULL_SUPPLIER))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testAcceptShouldCopyObject(String input, String from, String to, String key, String expected) {
        //given
        DocumentContext document = new ParseContextImpl().parse(input);
        JsonCopyRule rule = new JsonCopyRule(0, JsonPath.compile(from), JsonPath.compile(to), () -> key);

        //when
        rule.accept(document);

        //then
        Assertions.assertEquals(expected, document.jsonString());
    }

    @ParameterizedTest
    @MethodSource("invalidInputProvider")
    void testConstructorShouldNotAllowNulls(JsonPath path, JsonPath toPath, Supplier<String> keySupplier) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new JsonCopyRule(0, path, toPath, keySupplier));
    }
}