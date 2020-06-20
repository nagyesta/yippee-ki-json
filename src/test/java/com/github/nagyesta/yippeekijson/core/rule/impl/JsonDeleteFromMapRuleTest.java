package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.JsonMapperImpl;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Objects;

import static com.github.nagyesta.yippeekijson.core.rule.impl.JsonDeleteFromMapRule.*;
import static com.github.nagyesta.yippeekijson.core.rule.impl.JsonReplaceMapRule.RULE_NAME;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JsonDeleteFromMapRuleTest {

    @SuppressWarnings("unchecked")
    @Test
    void testApplyChangesShouldRemoveItemsAsIntended() {
        //given
        final String input = "{\"keep1\":\"keep\",\"keep2\":\"remove1\",\"remove1\":\"keep\",\"remove2\":\"remove\"}";
        final String path = "$";
        final JsonMapper jsonMapper = new JsonMapperImpl();
        final DocumentContext document = JsonPath.parse(input, jsonMapper.parserConfiguration());
        RawJsonRule raw = RawJsonRule.builder()
                .path(path)
                .name(RULE_NAME)
                .order(0)
                .putParams(Map.of(
                        PARAM_PREDICATE, Map.of(),
                        PARAM_KEEP_KEY, Map.of(),
                        PARAM_DELETE_KEY, Map.of(),
                        PARAM_KEEP_VALUE, Map.of(),
                        PARAM_DELETE_VALUE, Map.of()))
                .build();

        final FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        when(functionRegistry.lookupPredicate(anyMap())).thenReturn(
                Objects::nonNull,
                s -> String.valueOf(s).startsWith("keep"),
                s -> String.valueOf(s).startsWith("remove"),
                s -> String.valueOf(s).startsWith("keep"),
                s -> String.valueOf(s).startsWith("remove"));

        JsonDeleteFromMapRule underTest = new JsonDeleteFromMapRule(functionRegistry, jsonMapper, raw);

        //when
        underTest.accept(document);

        //then
        final String jsonString = document.jsonString();
        Assertions.assertEquals("{\"keep1\":\"keep\"}", jsonString);
    }
}
