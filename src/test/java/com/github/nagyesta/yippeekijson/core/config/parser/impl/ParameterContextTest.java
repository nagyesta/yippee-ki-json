package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.github.nagyesta.yippeekijson.core.test.params.ParamAnnotationHolder.*;

class ParameterContextTest {

    private static Stream<Arguments> validValueProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(ParameterContext.UseCase.VALUE, PARAM_DIFFERENT_NAME, PARAM_DIFFERENT_NAME_EXPECTED, false, false))
                .add(Arguments.of(ParameterContext.UseCase.VALUE, PARAM_SAME_NAME, PARAM_SAME_NAME, false, false))
                .add(Arguments.of(ParameterContext.UseCase.VALUE, PARAM_JAVAX_NULLABLE, PARAM_JAVAX_NULLABLE, false, true))
                .add(Arguments.of(ParameterContext.UseCase.VALUE, PARAM_SPRING_NULLABLE, PARAM_SPRING_NULLABLE_EXPECTED, false, true))
                .add(Arguments.of(ParameterContext.UseCase.VALUE, PARAM_NAMED_LIST, PARAM_NAMED_LIST_EXPECTED, true, false))
                .add(Arguments.of(ParameterContext.UseCase.MAP, PARAM_DIFFERENT_NAME, PARAM_DIFFERENT_NAME_EXPECTED, false, false))
                .add(Arguments.of(ParameterContext.UseCase.MAP, PARAM_SAME_NAME, PARAM_SAME_NAME, false, false))
                .add(Arguments.of(ParameterContext.UseCase.MAP, PARAM_JAVAX_NULLABLE, PARAM_JAVAX_NULLABLE, false, true))
                .add(Arguments.of(ParameterContext.UseCase.MAP, PARAM_SPRING_NULLABLE, PARAM_SPRING_NULLABLE_EXPECTED, false, true))
                .add(Arguments.of(ParameterContext.UseCase.MAP, PARAM_NAMED_LIST, PARAM_NAMED_LIST_EXPECTED, true, false))
                .add(Arguments.of(ParameterContext.UseCase.EMBEDDED, PARAM_DIFFERENT_NAME, PARAM_DIFFERENT_NAME_EXPECTED, false, false))
                .add(Arguments.of(ParameterContext.UseCase.EMBEDDED, PARAM_SAME_NAME, PARAM_SAME_NAME, false, false))
                .add(Arguments.of(ParameterContext.UseCase.EMBEDDED, PARAM_JAVAX_NULLABLE, PARAM_JAVAX_NULLABLE, false, true))
                .add(Arguments.of(ParameterContext.UseCase.EMBEDDED, PARAM_SPRING_NULLABLE, PARAM_SPRING_NULLABLE_EXPECTED, false, true))
                .add(Arguments.of(ParameterContext.UseCase.EMBEDDED, PARAM_NAMED_LIST, PARAM_NAMED_LIST_EXPECTED, true, false))
                .build();
    }

    private static Stream<Arguments> notAnnotated() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(ParameterContext.UseCase.VALUE, NOTHING))
                .add(Arguments.of(ParameterContext.UseCase.MAP, NOTHING))
                .add(Arguments.of(ParameterContext.UseCase.EMBEDDED, NOTHING))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validValueProvider")
    void testConstructorShouldParseValidParams(final ParameterContext.UseCase useCase,
                                               final String paramName,
                                               final String expectedName,
                                               final boolean expectedCollectionType,
                                               final boolean expectedNullable) {
        //given

        //when
        ParameterContext actual = getParamContextForUseCase(useCase, paramName);

        //then
        Assertions.assertEquals(actual.getName(), expectedName);
        Assertions.assertEquals(actual.getUseCase(), useCase);
        Assertions.assertEquals(actual.isCollectionTyped(), expectedCollectionType);
        Assertions.assertEquals(actual.isNullable(), expectedNullable);
    }

    @ParameterizedTest
    @MethodSource("notAnnotated")
    void testConstructorShouldParseValidParams(final ParameterContext.UseCase useCase,
                                               final String paramName) {
        //given

        //when + then exception
        Assertions.assertThrows(UnsupportedOperationException.class, () -> getParamContextForUseCaseUnchecked(useCase, paramName));
    }


}
