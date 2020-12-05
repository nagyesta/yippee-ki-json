package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.JsonMapperImpl;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.predicate.NotNullPredicate;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.nagyesta.yippeekijson.core.rule.impl.JsonReplaceMapRule.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@LaunchAbortArmed
class JsonReplaceMapRuleTest {

    private static final Predicate<Object> NON_NULL = Objects::nonNull;
    private static final Predicate<Object> IS_NULL = Objects::isNull;
    private static final Function<Map<String, Object>, Map<String, Object>> COPY_WITH_SUFFIX = map -> {
        final SortedSet<String> keySet = new TreeSet<>(map.keySet());
        final TreeMap<String, Object> treeMap = new TreeMap<>(map);
        keySet.forEach(key -> treeMap.put(key + "-1", map.get(key)));
        return treeMap;
    };
    private static final String X_A_2_B_C = "{\"x\":{\"a\":2,\"b\":\"c\"}}";
    private static final String X_A_2_A1_2_B_C_B1_C = "{\"x\":{\"a\":2,\"a-1\":2,\"b\":\"c\",\"b-1\":\"c\"}}";
    private static final String A_2_B_C = "{\"a\":2,\"b\":\"c\"}";
    private static final String A_2_A1_2_B_C_B1_C = "{\"a\":2,\"a-1\":2,\"b\":\"c\",\"b-1\":\"c\"}";
    private static final String ROOT = "$";
    private static final String ROOT_X = "$.x";
    private static final String ROOT_X_A = "$.x.a";

    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(X_A_2_B_C, ROOT_X, NON_NULL, COPY_WITH_SUFFIX, X_A_2_A1_2_B_C_B1_C))
                .add(Arguments.of(A_2_B_C, ROOT, NON_NULL, COPY_WITH_SUFFIX, A_2_A1_2_B_C_B1_C))
                .add(Arguments.of(A_2_B_C, ROOT_X, NON_NULL, COPY_WITH_SUFFIX, A_2_B_C))
                .add(Arguments.of(A_2_B_C, ROOT_X, IS_NULL, COPY_WITH_SUFFIX, A_2_B_C))
                .add(Arguments.of(A_2_B_C, ROOT, IS_NULL, COPY_WITH_SUFFIX, A_2_B_C))
                .build();
    }

    private static Stream<Arguments> invalidInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(X_A_2_B_C, ROOT_X_A, COPY_WITH_SUFFIX, IllegalStateException.class))
                .build();
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testAcceptShouldWorkOnMaps(final String input, final String path, final Predicate<Object> matches,
                                    final Function<Map<String, Object>, Map<String, Object>> transform, final String expected) {
        //given
        final JsonMapper jsonMapper = new JsonMapperImpl();
        final DocumentContext document = JsonPath.parse(input, jsonMapper.parserConfiguration());
        RawJsonRule raw = RawJsonRule.builder()
                .path(path)
                .name(RULE_NAME)
                .order(0)
                .putParams(Map.of(PARAM_MAP_FUNCTION, Map.of(), PARAM_PREDICATE, Map.of()))
                .build();

        final FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        when(functionRegistry.lookupFunction(anyMap())).thenReturn(o -> transform.apply((Map<String, Object>) o));
        when(functionRegistry.lookupPredicate(anyMap())).thenReturn(matches);

        final JsonReplaceMapRule rule = new JsonReplaceMapRule(functionRegistry, jsonMapper, raw);

        //when
        rule.accept(document);

        //then
        Assertions.assertEquals(expected, document.jsonString());
    }


    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @MethodSource("invalidInputProvider")
    void testAcceptShouldFailWhenNotUsedOnMaps(final String input, final String path,
                                               final Function<Map<String, Object>, Map<String, Object>> transform,
                                               final Class<? extends Exception> expected) {
        //given
        final JsonMapper jsonMapper = new JsonMapperImpl();
        final DocumentContext document = JsonPath.parse(input, jsonMapper.parserConfiguration());
        RawJsonRule raw = RawJsonRule.builder()
                .path(path)
                .name(RULE_NAME)
                .order(0)
                .putParams(Map.of(PARAM_MAP_FUNCTION, Map.of()))
                .build();

        final FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        when(functionRegistry.lookupFunction(anyMap())).thenReturn(o -> transform.apply((Map<String, Object>) o));

        final JsonReplaceMapRule rule = new JsonReplaceMapRule(functionRegistry, jsonMapper, raw);

        //when + then exception
        Assertions.assertThrows(expected, () -> rule.accept(document));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testToStringShouldContainClassName() {
        //given
        final JsonMapper jsonMapper = new JsonMapperImpl();
        final DocumentContext document = JsonPath.parse(A_2_B_C, jsonMapper.parserConfiguration());
        RawJsonRule raw = RawJsonRule.builder()
                .path(ROOT)
                .name(RULE_NAME)
                .order(0)
                .putParams(Map.of(PARAM_MAP_FUNCTION, Map.of()))
                .build();

        final FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        when(functionRegistry.lookupFunction(anyMap())).thenReturn(o -> COPY_WITH_SUFFIX.apply((Map<String, Object>) o));

        final JsonReplaceMapRule rule = new JsonReplaceMapRule(functionRegistry, jsonMapper, raw);

        //when
        final String actual = rule.toString();

        //then
        Assertions.assertTrue(actual.contains(JsonReplaceMapRule.class.getSimpleName()));
        Assertions.assertTrue(actual.contains("0"));
        Assertions.assertTrue(actual.contains(ROOT));
        Assertions.assertTrue(actual.contains(NotNullPredicate.class.getSimpleName()));
    }
}
