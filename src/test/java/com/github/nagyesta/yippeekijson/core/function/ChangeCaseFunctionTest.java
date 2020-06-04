package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.function.ChangeCaseFunction.Case;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Arrays;
import java.util.stream.Stream;

class ChangeCaseFunctionTest {

    private static final String FOO_BAR_CAPITAL = "Foo bar";
    private static final String FOO_BAR = "foo bar";
    private static final String FOO_BAR_UPPER = "FOO BAR";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String FOO_BAR_UNCAPITAL = "fOO BAR";

    private static Stream<Arguments> toStringProvider() {
        return Arrays.stream(Case.values())
                .map(c -> {
                    if (c.ordinal() % 2 == 0) {
                        return c.name();
                    } else {
                        return c.name().toLowerCase();
                    }
                })
                .map(Arguments::of);
    }

    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(Case.CAPITALIZED, null, null))
                .add(Arguments.of(Case.CAPITALIZED, FOO_BAR, FOO_BAR_CAPITAL))
                .add(Arguments.of(Case.CAPITALIZED, FOO_BAR_UPPER, FOO_BAR_UPPER))
                .add(Arguments.of(Case.UNCAPITALIZED, FOO_BAR_UPPER, FOO_BAR_UNCAPITAL))
                .add(Arguments.of(Case.UNCAPITALIZED, FOO_BAR, FOO_BAR))
                .add(Arguments.of(Case.UPPER_CASE, FOO_BAR, FOO_BAR_UPPER))
                .add(Arguments.of(Case.UPPER_CASE, FOO_BAR_CAPITAL, FOO_BAR_UPPER))
                .add(Arguments.of(Case.UPPER_CASE, FOO_BAR_UPPER, FOO_BAR_UPPER))
                .add(Arguments.of(Case.LOWER_CASE, FOO_BAR_UPPER, FOO_BAR))
                .add(Arguments.of(Case.LOWER_CASE, FOO_BAR_CAPITAL, FOO_BAR))
                .add(Arguments.of(Case.LOWER_CASE, FOO_BAR_UNCAPITAL, FOO_BAR))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testApplyShouldWorkForValidInout(final Case to, final String input, final String expected) {
        //given
        ChangeCaseFunction underTest = new ChangeCaseFunction(to.name());

        //when
        final String actual = underTest.apply(input);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testConstructorShouldNotAllowNulls(final String to) {
        //given;
        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ChangeCaseFunction(to));
    }

    @ParameterizedTest
    @MethodSource("toStringProvider")
    void testToStringShouldContainClassNameAndParameters(final String caseValue) {
        //given
        final ChangeCaseFunction underTest = new ChangeCaseFunction(caseValue);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(ChangeCaseFunction.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(caseValue.toUpperCase()));
    }
}
