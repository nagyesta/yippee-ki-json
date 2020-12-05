package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

@LaunchAbortArmed
class DecimalAddFunctionTest {

    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, "1", 0, null))
                .add(Arguments.of("0", "1", 0, "1"))
                .add(Arguments.of("0", "1", 2, "1.00"))
                .add(Arguments.of("3.002", "1.003", 2, "4.01"))
                .add(Arguments.of("4", "1.5", 0, "6"))
                .build();
    }

    private static Stream<Arguments> toStringProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(new BigDecimal("42.3"), 1))
                .add(Arguments.of(new BigDecimal("42"), 2))
                .build();
    }

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(BigDecimal.TEN, null))
                .add(Arguments.of(null, 1))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testApplyShouldAddNumbersProperly(final String a, final String b, final Integer scale, final String expected) {
        //given
        DecimalAddFunction underTest = new DecimalAddFunction(new BigDecimal(b), scale);
        final BigDecimal value;
        if (a == null) {
            value = null;
        } else {
            value = new BigDecimal(a);
        }

        //when
        final BigDecimal actual = underTest.apply(value);

        //then
        if (expected == null) {
            Assertions.assertNull(actual);
        } else {
            Assertions.assertEquals(expected, actual.toString());
        }
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldNotAllowNulls(final BigDecimal operand, final Integer scale) {
        //given;
        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DecimalAddFunction(operand, scale));
    }

    @ParameterizedTest
    @MethodSource("toStringProvider")
    void testToStringShouldContainClassNameAndParameters(final BigDecimal operand, final Integer scale) {
        //given
        final DecimalAddFunction underTest = new DecimalAddFunction(operand, scale);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(DecimalAddFunction.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(operand.toString()));
        Assertions.assertTrue(actual.contains(String.valueOf(scale)));
    }
}
