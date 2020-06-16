package com.github.nagyesta.yippeekijson.core.function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class LiteralReplaceFunctionTest {

    private static final String FOO = "foo";
    private static final String BAR = "bar";
    private static final String BAZ = "baz";
    private static final String R = "r";
    private static final String Z = "z";
    private static final String Z_UPPER = "Z";

    private static Stream<Arguments> toStringProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(FOO, BAR))
                .add(Arguments.of(BAZ, Z_UPPER))
                .build();
    }

    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(FOO, BAR, null, null))
                .add(Arguments.of(FOO, BAR, FOO, BAR))
                .add(Arguments.of(FOO, BAR, FOO + FOO + BAZ, BAR + BAR + BAZ))
                .add(Arguments.of(FOO, BAR, BAZ, BAZ))
                .add(Arguments.of(R, Z, BAR, BAZ))
                .add(Arguments.of(Z_UPPER, Z, BAZ, BAZ))
                .build();
    }

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(FOO, null))
                .add(Arguments.of(null, BAR))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testApplyShouldWorkForValidInout(final String find, final String replace,
                                          final String input, final String expected) {
        //given
        LiteralReplaceFunction underTest = new LiteralReplaceFunction(find, replace);

        //when
        final String actual = underTest.apply(input);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldNotAllowNulls(final String find, final String replace) {
        //given;
        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new LiteralReplaceFunction(find, replace));
    }

    @ParameterizedTest
    @MethodSource("toStringProvider")
    void testToStringShouldContainClassNameAndParameters(final String find, final String replace) {
        //given
        final LiteralReplaceFunction underTest = new LiteralReplaceFunction(find, replace);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(LiteralReplaceFunction.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(find));
        Assertions.assertTrue(actual.contains(replace));
    }
}
