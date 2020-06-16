package com.github.nagyesta.yippeekijson.core.function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

class DecimalSubtractFunctionTest {

    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of("0", "1", "0", "-1"))
                .add(Arguments.of("0", "1", "2", "-1.00"))
                .add(Arguments.of("3.002", "1.003", "2", "2.00"))
                .add(Arguments.of("4", "1.5", "0", "3"))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testApplyShouldSubtractNumbersProperly(final String a, final String b, final String scale, final String expected) {
        //given
        DecimalSubtractFunction underTest = new DecimalSubtractFunction(b, scale);

        //when
        final BigDecimal actual = underTest.apply(new BigDecimal(a));

        //then
        Assertions.assertEquals(expected, actual.toString());
    }
}
