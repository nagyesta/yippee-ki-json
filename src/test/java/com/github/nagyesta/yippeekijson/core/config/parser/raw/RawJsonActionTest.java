package com.github.nagyesta.yippeekijson.core.config.parser.raw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

class RawJsonActionTest {

    private static final String ACTION = "action";

    private static Object[][] nullProvider() {
        return new Object[][]{
                {null, null},
                {ACTION, null},
                {null, Collections.emptyList()}
        };
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testSettersShouldThrowExceptionsWhenCalledWithNulls(final String name, final List<RawJsonRule> rules) {
        //given
        final RawJsonAction underTest = new RawJsonAction();

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            underTest.setName(name);
            underTest.setRules(rules);
        });
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = ACTION)
    void testEqualsShouldUseOnlyName(final String name) {
        //given
        final RawJsonAction first = new RawJsonAction();
        first.setName(name);
        first.setRules(List.of(new RawJsonRule()));
        final RawJsonAction second = new RawJsonAction();
        second.setName(name);

        //when
        final boolean actual = Objects.equals(first, second);

        //then
        Assertions.assertTrue(actual);
    }

    @SuppressWarnings({"EqualsWithItself", "ConstantConditions"})
    @Test
    void testEqualsShouldBeDefaultTrueIfSame() {
        //given
        final RawJsonAction first = new RawJsonAction();
        first.setName(ACTION);

        //when
        final boolean actual = first.equals(first);

        //then
        Assertions.assertTrue(actual);
    }

    @Test
    void testEqualsShouldBeDefaultFalseIfDifferentClass() {
        //given
        final RawJsonAction first = new RawJsonAction();
        first.setName(ACTION);

        //when
        final boolean actual = first.equals(new Object());

        //then
        Assertions.assertFalse(actual);
    }
}
