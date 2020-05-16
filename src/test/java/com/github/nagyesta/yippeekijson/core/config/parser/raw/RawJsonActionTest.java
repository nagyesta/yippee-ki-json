package com.github.nagyesta.yippeekijson.core.config.parser.raw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Objects;

class RawJsonActionTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "action")
    void testEqualsShouldUseOnlyName(String name) {
        //given
        RawJsonAction first = new RawJsonAction();
        first.setName(name);
        first.setRules(List.of(new RawJsonRule()));
        RawJsonAction second = new RawJsonAction();
        second.setName(name);

        //when
        final boolean actual = Objects.equals(first, second);

        //then
        Assertions.assertTrue(actual);
    }
}