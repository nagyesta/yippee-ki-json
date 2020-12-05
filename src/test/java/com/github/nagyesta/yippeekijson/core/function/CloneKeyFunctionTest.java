package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

@LaunchAbortArmed
class CloneKeyFunctionTest {

    private static final String FROM = "from";
    private static final String FROM_KEY = "from-key";
    private static final String TO = "to";
    private static final int INT_42 = 42;
    private static final String TO_KEY = "to-key";

    private static Stream<Arguments> toStringProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(FROM, TO))
                .add(Arguments.of(FROM_KEY, TO_KEY))
                .build();
    }

    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(FROM_KEY, TO_KEY, null, null))
                .add(Arguments.of(FROM_KEY, TO_KEY, Map.of(FROM_KEY, FROM), Map.of(FROM_KEY, FROM, TO_KEY, FROM)))
                .add(Arguments.of(FROM_KEY, TO_KEY, Map.of(FROM_KEY, FROM, TO_KEY, TO), Map.of(FROM_KEY, FROM, TO_KEY, FROM)))
                .add(Arguments.of(FROM_KEY, TO_KEY, Map.of(TO_KEY, TO), Map.of(TO_KEY, TO)))
                .add(Arguments.of(FROM_KEY, TO_KEY, Map.of(FROM_KEY, INT_42, TO_KEY, TO), Map.of(FROM_KEY, INT_42, TO_KEY, INT_42)))
                .build();
    }

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(FROM_KEY, null))
                .add(Arguments.of(null, TO_KEY))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testApplyShouldWorkForValidInout(final String from, final String to,
                                          final Map<String, Object> input, final Map<String, Object> expected) {
        //given
        CloneKeyFunction underTest = new CloneKeyFunction(from, to);

        //when
        final Map<String, Object> actual = underTest.apply(input);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldNotAllowNulls(final String from, final String to) {
        //given;
        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CloneKeyFunction(from, to));
    }

    @ParameterizedTest
    @MethodSource("toStringProvider")
    void testToStringShouldContainClassNameAndParameters(final String from, final String to) {
        //given
        final CloneKeyFunction underTest = new CloneKeyFunction(from, to);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(CloneKeyFunction.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(from));
        Assertions.assertTrue(actual.contains(to));
    }
}
