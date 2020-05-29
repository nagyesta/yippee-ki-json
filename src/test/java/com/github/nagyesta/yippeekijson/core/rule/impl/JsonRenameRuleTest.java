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

class JsonRenameRuleTest {

    private static final Supplier<String> C = () -> "c";
    private static final Supplier<String> D = () -> "d";
    private static final String A_C_FOO_B_C_BAR = "{\"a\":{\"c\":\"foo\"},\"b\":{\"c\":\"bar\"}}";
    private static final String A_D_FOO_B_D_BAR = "{\"a\":{\"d\":\"foo\"},\"b\":{\"d\":\"bar\"}}";
    private static final String A_C_FOO_B_D_BAR = "{\"a\":{\"c\":\"foo\"},\"b\":{\"d\":\"bar\"}}";
    private static final String ANY_NODE = "$.*";
    private static final String NODE_NAMED_B_ANYWHERE = "$..b";

    private static Stream<Arguments> validInputProvider() {
        return Stream.of(
                Arguments.of(A_C_FOO_B_C_BAR, ANY_NODE, C, D, A_D_FOO_B_D_BAR),
                Arguments.of(A_D_FOO_B_D_BAR, ANY_NODE, D, C, A_C_FOO_B_C_BAR),
                Arguments.of(A_C_FOO_B_C_BAR, NODE_NAMED_B_ANYWHERE, C, D, A_C_FOO_B_D_BAR),
                Arguments.of(A_C_FOO_B_C_BAR, ANY_NODE, D, D, A_C_FOO_B_C_BAR), //skip if names are the same
                Arguments.of(A_C_FOO_B_C_BAR, ANY_NODE, D, C, A_C_FOO_B_C_BAR)  //skip if not matched
        );
    }

    private static Stream<Arguments> invalidInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null, null))
                .add(Arguments.of(JsonPath.compile(ANY_NODE), null, null))
                .add(Arguments.of(null, C, null))
                .add(Arguments.of(null, null, C))
                .add(Arguments.of(JsonPath.compile(ANY_NODE), C, null))
                .add(Arguments.of(JsonPath.compile(ANY_NODE), null, C))
                .add(Arguments.of(null, C, C))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testAcceptShouldRenameNodes(final String input, final String path, final Supplier<String> oldKey, final Supplier<String> newKey,
                                     final String expected) {
        //given
        final DocumentContext document = new ParseContextImpl().parse(input);
        final JsonRenameRule rule = new JsonRenameRule(0, JsonPath.compile(path), oldKey, newKey);

        //when
        rule.accept(document);

        //then
        Assertions.assertEquals(expected, document.jsonString());
    }

    @ParameterizedTest
    @MethodSource("invalidInputProvider")
    void testConstructorShouldNotAllowNulls(final JsonPath path, final Supplier<String> oldSupplier, final Supplier<String> newSupplier) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new JsonRenameRule(0, path, oldSupplier, newSupplier));
    }
}
