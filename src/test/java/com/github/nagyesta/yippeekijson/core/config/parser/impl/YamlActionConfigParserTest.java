package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.config.entities.JsonAction;
import com.github.nagyesta.yippeekijson.core.config.entities.JsonActions;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonRuleRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonAction;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonActions;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import com.github.nagyesta.yippeekijson.core.rule.AbstractJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.JsonRule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class YamlActionConfigParserTest {

    @SuppressWarnings("checkstyle:MagicNumber")
    private static Stream<Arguments> rawActionProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(rawAction(3), 3))
                .add(Arguments.of(rawAction(1), 1))
                .add(Arguments.of(rawAction(0), 0))
                .add(Arguments.of(null, 0))
                .build();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private static Stream<Arguments> rawActionsProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(rawActions(3), 3))
                .add(Arguments.of(rawActions(1), 1))
                .add(Arguments.of(rawActions(0), 0))
                .add(Arguments.of(null, 0))
                .build();
    }

    private static RawJsonActions rawActions(final int total) {
        final List<RawJsonAction> actions = IntStream.rangeClosed(1, total)
                .mapToObj(YamlActionConfigParserTest::rawAction)
                .collect(Collectors.toList());
        final RawJsonActions result = new RawJsonActions();
        result.setActions(actions);
        return result;
    }

    private static RawJsonAction rawAction(final int i) {
        final RawJsonAction action = new RawJsonAction();
        action.setName(actionName(i));
        action.setRules(rawRuleList(i));
        return action;
    }

    private static String actionName(final int i) {
        return "action-name-" + i;
    }

    private static List<RawJsonRule> rawRuleList(final int i) {
        return IntStream.rangeClosed(1, i)
                .mapToObj(index -> {
                    final RawJsonRule rule = new RawJsonRule();
                    rule.setName(ruleName(index));
                    rule.setPath(pathName(index));
                    return spy(rule);
                })
                .collect(Collectors.toList());
    }

    private static String ruleName(final int index) {
        return "rule-" + index;
    }

    private static String pathName(final int index) {
        return "$.path[" + index + "]";
    }

    @Test
    void testConstructorShouldThrowExceptionWhenNullProvided() {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new YamlActionConfigParser(null));
    }

    @Test
    void testParseShouldNotParseToRawThenConvertWhenNullStreamProvided() throws ConfigParseException {
        //given
        final JsonRuleRegistry jsonRuleRegistry = mock(JsonRuleRegistry.class);

        final YamlActionConfigParser underTest = spy(new YamlActionConfigParser(jsonRuleRegistry));

        //when
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.parse((InputStream) null));

        //then
        final InOrder inOrder = inOrder(underTest, jsonRuleRegistry);
        inOrder.verify(underTest).parse((InputStream) isNull());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testParseShouldParseToRawThenConvertWhenValidStreamProvided() throws ConfigParseException {
        //given
        final JsonRuleRegistry jsonRuleRegistry = mock(JsonRuleRegistry.class);
        final InputStream stream = mock(InputStream.class);
        final JsonActions expected = JsonActions.builder().build();
        final RawJsonActions rawJsonActions = new RawJsonActions();

        final YamlActionConfigParser underTest = spy(new YamlActionConfigParser(jsonRuleRegistry));
        doReturn(expected).when(underTest).convertActions(eq(rawJsonActions));
        doReturn(rawJsonActions).when(underTest).parseAsRawJsonActions(eq(stream));

        //when
        final JsonActions actual = underTest.parse(stream);

        //then
        Assertions.assertEquals(expected, actual);
        final InOrder inOrder = inOrder(underTest, jsonRuleRegistry);
        inOrder.verify(underTest).parse(eq(stream));
        inOrder.verify(underTest).parseAsRawJsonActions(eq(stream));
        inOrder.verify(underTest).convertActions(eq(rawJsonActions));
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource("rawActionsProvider")
    void testConvertActionsShouldCallConvertSingleEachTimeWhenCalledWithValidInput(
            final RawJsonActions actions, final int expectedActions) {
        //given
        final JsonRuleRegistry jsonRuleRegistry = mock(JsonRuleRegistry.class);

        final YamlActionConfigParser underTest = spy(new YamlActionConfigParser(jsonRuleRegistry));
        // single rule conversion can be mocked here
        doAnswer(arg -> {
            final RawJsonAction raw = arg.getArgument(0, RawJsonAction.class);
            return JsonAction.builder()
                    .name(raw.getName())
                    .build();
        }).when(underTest).convertSingleAction(any(RawJsonAction.class));

        //when
        final JsonActions actual = underTest.convertActions(actions);

        //then
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getActions());
        Assertions.assertEquals(expectedActions, actual.getActions().size());
        IntStream.rangeClosed(1, expectedActions)
                .forEach(index -> {
                    final String expectedName = actionName(index);
                    Assertions.assertNotNull(actual.getActions().get(expectedName));
                    Assertions.assertEquals(expectedName, actual.getActions().get(expectedName).getName());
                    Assertions.assertNotNull(actual.getActions().get(expectedName).getRules());
                });

        final InOrder inOrder = inOrder(underTest, jsonRuleRegistry);
        inOrder.verify(underTest).convertActions(eq(actions));
        inOrder.verify(underTest, times(expectedActions)).convertSingleAction(any(RawJsonAction.class));
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource("rawActionProvider")
    void testConvertSingleActionShouldCallRegistryForEachRule(final RawJsonAction action, final int expectedRules) {
        //given
        final JsonRuleRegistry jsonRuleRegistry = mock(JsonRuleRegistry.class);

        final YamlActionConfigParser underTest = spy(new YamlActionConfigParser(jsonRuleRegistry));
        // single rule conversion can be mocked here
        when(jsonRuleRegistry.newInstanceFrom(any(RawJsonRule.class))).thenAnswer(arg -> {
            final RawJsonRule raw = arg.getArgument(0, RawJsonRule.class);
            final JsonPath path = mock(JsonPath.class);
            doReturn(raw.getPath()).when(path).getPath();
            return spy(new AbstractJsonRule(raw.getOrder(), path) {
                @Override
                public void accept(final DocumentContext documentContext) {
                }
            });
        });

        //when
        final JsonAction actual = underTest.convertSingleAction(action);

        //then
        if (expectedRules > 0) {
            Assertions.assertNotNull(actual);
            Assertions.assertEquals(actionName(expectedRules), actual.getName());
            Assertions.assertNotNull(actual.getRules());
            IntStream.rangeClosed(1, expectedRules)
                    .forEach(index -> {
                        final JsonRule rule = actual.getRules().get(index - 1);
                        Assertions.assertNotNull(rule);
                        Assertions.assertEquals(index - 1, rule.getOrder());

                        final JsonPath ruleJsonPath = rule.getJsonPath();
                        Assertions.assertEquals(pathName(index), ruleJsonPath.getPath());
                    });
        }
        final InOrder inOrder = inOrder(underTest, jsonRuleRegistry);
        inOrder.verify(underTest).convertSingleAction(eq(action));
        if (action != null) {
            inOrder.verify(underTest).reindexRules(eq(action));
            inOrder.verify(jsonRuleRegistry, times(expectedRules)).newInstanceFrom(any(RawJsonRule.class));
        }
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testReindexRulesShouldInsertIndices() {
        //given
        final JsonRuleRegistry jsonRuleRegistry = mock(JsonRuleRegistry.class);
        final YamlActionConfigParser underTest = spy(new YamlActionConfigParser(jsonRuleRegistry));

        final RawJsonAction rawJsonAction = rawAction(10);
        Assertions.assertTrue(rawJsonAction.getRules().stream().allMatch(r -> r.getOrder() == null));

        //when
        underTest.reindexRules(rawJsonAction);

        //then
        Assertions.assertTrue(rawJsonAction.getRules().stream().noneMatch(r -> r.getOrder() == null));
    }
}
