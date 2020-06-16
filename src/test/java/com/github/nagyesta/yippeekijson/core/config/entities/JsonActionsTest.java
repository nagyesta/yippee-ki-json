package com.github.nagyesta.yippeekijson.core.config.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class JsonActionsTest {
    private static final String ACTION = "action";

    private static Object[][] nullProvider() {
        return new Object[][]{
                {null, null},
                {ACTION, null},
                {null, JsonAction.builder().build()}
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
                .addAction(ACTION, JsonAction.builder().build())
                .addAction(ACTION, JsonAction.builder().build()));
    }
}
