package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.function.helper.CaseChange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Arrays;
import java.util.stream.Stream;

class ChangeCaseFunctionTest {

    private static final String FOO_BAR_CAPITAL = "Foo bar";
    private static final String FOO_BAR = "foo bar";
    private static final String FOO_BAR_UPPER = "FOO BAR";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String FOO_BAR_UNCAPITAL = "fOO BAR";

    private static Stream<Arguments> toStringProvider() {
        return Arrays.stream(CaseChange.values())
                .map(CaseChange::name)
                .map(Arguments::of);
    }

    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(CaseChange.CAPITALIZED, null, null))
                .add(Arguments.of(CaseChange.CAPITALIZED, FOO_BAR, FOO_BAR_CAPITAL))
                .add(Arguments.of(CaseChange.CAPITALIZED, FOO_BAR_UPPER, FOO_BAR_UPPER))
                .add(Arguments.of(CaseChange.UNCAPITALIZED, FOO_BAR_UPPER, FOO_BAR_UNCAPITAL))
                .add(Arguments.of(CaseChange.UNCAPITALIZED, FOO_BAR, FOO_BAR))
                .add(Arguments.of(CaseChange.UPPER_CASE, FOO_BAR, FOO_BAR_UPPER))
                .add(Arguments.of(CaseChange.UPPER_CASE, FOO_BAR_CAPITAL, FOO_BAR_UPPER))
                .add(Arguments.of(CaseChange.UPPER_CASE, FOO_BAR_UPPER, FOO_BAR_UPPER))
                .add(Arguments.of(CaseChange.LOWER_CASE, FOO_BAR_UPPER, FOO_BAR))
                .add(Arguments.of(CaseChange.LOWER_CASE, FOO_BAR_CAPITAL, FOO_BAR))
                .add(Arguments.of(CaseChange.LOWER_CASE, FOO_BAR_UNCAPITAL, FOO_BAR))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testApplyShouldWorkForValidInout(final CaseChange to, final String input, final String expected) {
        //given
        ChangeCaseFunction underTest = new ChangeCaseFunction(to);

        //when
        final String actual = underTest.apply(input);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @NullSource
    void testConstructorShouldNotAllowNulls(final CaseChange to) {
        //given;
        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ChangeCaseFunction(to));
    }

    @ParameterizedTest
    @MethodSource("toStringProvider")
    void testToStringShouldContainClassNameAndParameters(final CaseChange caseValue) {
        //given
        final ChangeCaseFunction underTest = new ChangeCaseFunction(caseValue);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(ChangeCaseFunction.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(caseValue.name()));
    }
}
