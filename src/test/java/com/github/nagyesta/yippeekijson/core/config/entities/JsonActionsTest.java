package com.github.nagyesta.yippeekijson.core.config.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import static org.mockito.Mockito.mock;

class JsonActionsTest {
    private static final String ACTION = "action";

    private static Object[][] nullProvider() {
        return new Object[][]{
                {null, null},
                {ACTION, null},
                {null, mock(JsonAction.class)}
        };
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testBuilderMethodsShouldThrowExceptionsWhenCalledWithNulls(final String name, final JsonAction action) {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> JsonActions.builder().addAction(name, action));
    }

    @Test
    void testBuilderMethodsShouldThrowExceptionsWhenCalledWithSameNameTwice() {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> JsonActions.builder()
                .addAction(ACTION, mock(JsonAction.class))
                .addAction(ACTION, mock(JsonAction.class)));
    }

    @ParameterizedTest
    @NullSource
    void testConstructorShouldThrowExceptionsWhenCalledWithNull(final JsonActions.JsonActionsBuilder builder) {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new JsonActions(builder));
    }
}
