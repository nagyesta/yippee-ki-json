package com.github.nagyesta.yippeekijson.core.config.parser.raw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.util.List;

class RawJsonActionsTest {

    @ParameterizedTest
    @NullSource
    void testSettersShouldThrowExceptionsWhenCalledWithNulls(final List<RawJsonAction> actions) {
        //given
        final RawJsonActions underTest = new RawJsonActions();

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.setActions(actions));
    }
}
