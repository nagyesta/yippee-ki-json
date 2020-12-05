package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.JsonMapperImpl;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.github.nagyesta.yippeekijson.core.rule.impl.JsonDeleteRule.RULE_NAME;

@LaunchAbortArmed
class JsonDeleteRuleTest {

    private static final String A_C_FOO = "{\"a\":{\"c\":\"foo\"}}";
    private static final String A_C_FOO_B_C_BAR = "{\"a\":{\"c\":\"foo\"},\"b\":{\"c\":\"bar\"}}";
    private static final String A_C_FOO_E_D_BAZ_B_C_BAR = "{\"a\":{\"c\":\"foo\",\"e\":{\"d\":\"baz\"}},\"b\":{\"c\":\"bar\"}}";
    private static final String NODE_NAMED_E_WITH_A_PARENT = "$..a.e";
    private static final String NODE_WITH_MATCHING_CHILD = "$..*[?(@.c=='bar' || @.d=='baz')]";
    private static final String NODE_NAMED_B = "$.b";
    private static final String ROOT = "$.*";

    private static Stream<Arguments> validInputProvider() {
        return Stream.of(
                Arguments.of(A_C_FOO_E_D_BAZ_B_C_BAR, NODE_NAMED_E_WITH_A_PARENT, A_C_FOO_B_C_BAR),
                Arguments.of(A_C_FOO_E_D_BAZ_B_C_BAR, NODE_WITH_MATCHING_CHILD, A_C_FOO),
                Arguments.of(A_C_FOO_B_C_BAR, NODE_NAMED_B, A_C_FOO),
                Arguments.of(A_C_FOO_B_C_BAR, ROOT, "{}")
        );
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testAcceptShouldDeleteNodes(final String input, final String path, final String expected) {
        //given
        final JsonMapper jsonMapper = new JsonMapperImpl();
        final DocumentContext document = JsonPath.parse(input, jsonMapper.parserConfiguration());
        RawJsonRule raw = RawJsonRule.builder()
                .path(path)
                .name(RULE_NAME)
                .order(0)
                .build();

        final JsonDeleteRule rule = new JsonDeleteRule(raw);

        //when
        rule.accept(document);

        //then
        Assertions.assertEquals(expected, document.jsonString());
    }
}
