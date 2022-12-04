package com.github.nagyesta.yippeekijson.core.config.validation;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@LaunchAbortArmed
class JsonPathValidatorTest {

    @JsonPath(message = "message")
    private static final String MESSAGE = "MESSAGE";

    private static Stream<Arguments> inputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, false))
                .add(Arguments.of("$..", false))
                .add(Arguments.of("$.a", true))
                .add(Arguments.of("$.a.s.", false))
                .build();
    }

    @ParameterizedTest
    @MethodSource("inputProvider")
    void testIsValidShouldMatchAsExpected(final String input, final boolean valid) throws NoSuchFieldException {
        //given
        JsonPathValidator underTest = new JsonPathValidator();
        underTest.initialize(this.getClass().getDeclaredField(MESSAGE).getAnnotation(JsonPath.class));
        final ConstraintValidatorContext constraintValidatorContext = mock(ConstraintValidatorContext.class);
        final ConstraintViolationBuilder constraintViolationBuilder = mock(ConstraintViolationBuilder.class);
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);

        //when
        final boolean actual = underTest.isValid(input, constraintValidatorContext);

        //then
        Assertions.assertEquals(valid, actual);
    }
}
