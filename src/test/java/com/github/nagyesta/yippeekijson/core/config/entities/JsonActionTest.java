package com.github.nagyesta.yippeekijson.core.config.entities;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.rule.JsonRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.mockito.Mockito.mock;

@LaunchAbortArmed
class JsonActionTest {

    private static final String ACTION = "action";

    private static Object[][] nullProvider() {
        return new Object[][]{
                {null, null},
                {ACTION, null},
                {null, mock(JsonRule.class)}
        };
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testBuilderMethodsShouldThrowExceptionsWhenCalledWithNulls(final String name, final JsonRule rule) {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> JsonAction.builder().name(name).addRule(rule));
    }
}
