package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.AbstractJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.JsonRule;
import com.github.nagyesta.yippeekijson.core.rule.impl.JsonDeleteRule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

class JsonRuleRegistryImplTest {

    private static final String DELETE = "delete";
    private static final String PATH = "$..a";
    private static final String FAILING = "failing";
    private static final String WRONG = "WRONG";

    private static Stream<Arguments> nullListProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(mock(FunctionRegistry.class), null))
                .add(Arguments.of(null, Collections.emptyList()))
                .build();
    }

    private static Stream<Arguments> invalidRuleProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, IllegalArgumentException.class))
                .add(Arguments.of(new RawJsonRule(), IllegalArgumentException.class))
                .add(Arguments.of(rawJsonRule(DELETE, null), IllegalArgumentException.class))
                .add(Arguments.of(rawJsonRule(null, 1), IllegalArgumentException.class))
                .add(Arguments.of(rawJsonRule(FAILING, 1), IllegalStateException.class))
                .build();
    }

    private static Stream<Arguments> invalidRuleClassProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(Collections.singletonList(null), IllegalArgumentException.class))
                .add(Arguments.of(List.of(WrongRule.class), IllegalArgumentException.class))
                .add(Arguments.of(List.of(NotAnnotatedRule.class), IllegalArgumentException.class))
                .add(Arguments.of(List.of(FailingRule.class, FailingRule.class), IllegalArgumentException.class))
                .build();
    }

    private static RawJsonRule rawJsonRule(String name, Integer order) {
        final RawJsonRule source = new RawJsonRule();
        source.setName(name);
        source.setOrder(order);
        source.setPath(JsonRuleRegistryImplTest.PATH);
        return source;
    }

    @ParameterizedTest
    @MethodSource("nullListProvider")
    void testConstructorShouldFailIfNullProvided(FunctionRegistry functionRegistry,
                                                 List<Class<? extends JsonRule>> rules) {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new JsonRuleRegistryImpl(functionRegistry, rules));
    }

    @ParameterizedTest
    @MethodSource("invalidRuleClassProvider")
    void testConstructorShouldFailIfWrongClassProvided(List<Class<? extends JsonRule>> rules, Class<? extends Exception> exception) {
        //given
        final FunctionRegistry functionRegistry = mock(FunctionRegistry.class);

        //when + then exception
        Assertions.assertThrows(exception, () -> new JsonRuleRegistryImpl(functionRegistry, rules));
    }

    @Test
    void testNewInstanceFromShouldReturnAnInstanceWhenFound() {
        //given
        final FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        JsonRuleRegistryImpl underTest = new JsonRuleRegistryImpl(functionRegistry, Collections.emptyList());
        underTest.registerRuleClass(JsonDeleteRule.class);

        final Integer order = 1;
        final RawJsonRule source = rawJsonRule(DELETE, order);
        //when
        final JsonRule actual = underTest.newInstanceFrom(source);

        //then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(JsonDeleteRule.class, actual.getClass());
        Assertions.assertEquals(order, actual.getOrder());
    }

    @Test
    void registerRuleClass() {
    }

    @ParameterizedTest
    @MethodSource("invalidRuleProvider")
    void testNewInstanceFromShouldFailForInvalidInput(RawJsonRule source, Class<? extends Exception> exception) {
        //given
        final FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        JsonRuleRegistryImpl underTest = new JsonRuleRegistryImpl(functionRegistry, Collections.emptyList());
        underTest.registerRuleClass(JsonDeleteRule.class);
        underTest.registerRuleClass(FailingRule.class);

        //when
        Assertions.assertThrows(exception, () -> underTest.newInstanceFrom(source));
    }

    private static class WrongRule extends AbstractJsonRule {

        @SuppressWarnings("unused")
        @NamedRule(WRONG)
        protected WrongRule(int order, JsonPath jsonPath, boolean fail) {
            super(order, jsonPath);
        }

        @Override
        public void accept(DocumentContext documentContext) {
            //
        }
    }

    private static class FailingRule extends WrongRule {
        protected FailingRule(int order, JsonPath jsonPath) {
            super(order, jsonPath, true);
            if (order > 0) throw new RuntimeException(FAILING);
        }
        @NamedRule(FAILING)
        private FailingRule(FunctionRegistry functionRegistry, RawJsonRule rule) {
            this(rule.getOrder(), JsonPath.compile(rule.getPath()));
        }
    }

    private static class NotAnnotatedRule extends WrongRule {
        protected NotAnnotatedRule(int order, JsonPath jsonPath) {
            super(order, jsonPath, true);
            if (order > 0) throw new RuntimeException(FAILING);
        }
    }


}