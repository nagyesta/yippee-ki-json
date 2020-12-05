package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

@LaunchAbortArmed
class DecimalMultiplyFunctionTest {

    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of("0", "1", 0, "0"))
                .add(Arguments.of("0", "1", 2, "0.00"))
                .add(Arguments.of("3.002", "1.003", 2, "3.01"))
                .add(Arguments.of("4", "1.5", 0, "6"))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testApplyShouldMultiplyNumbersProperly(final String a, final String b, final Integer scale, final String expected) {
        //given
        DecimalMultiplyFunction underTest = new DecimalMultiplyFunction(new BigDecimal(b), scale);

        //when
        final BigDecimal actual = underTest.apply(new BigDecimal(a));

        //then
        Assertions.assertEquals(expected, actual.toString());
    }
}
