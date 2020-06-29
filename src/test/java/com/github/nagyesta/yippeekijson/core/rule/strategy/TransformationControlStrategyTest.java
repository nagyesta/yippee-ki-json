package com.github.nagyesta.yippeekijson.core.rule.strategy;

import com.github.nagyesta.yippeekijson.core.exception.AbortTransformationException;
import com.github.nagyesta.yippeekijson.core.exception.StopRuleProcessingException;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

class TransformationControlStrategyTest {

    private static Stream<Arguments> inputSupplier() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(TransformationControlStrategy.ABORT, notEmptySet(), AbortTransformationException.class))
                .add(Arguments.of(TransformationControlStrategy.CONTINUE, notEmptySet(), null))
                .add(Arguments.of(TransformationControlStrategy.SKIP_REST, notEmptySet(), StopRuleProcessingException.class))
                .add(Arguments.of(TransformationControlStrategy.ABORT, Set.of(), null))
                .add(Arguments.of(TransformationControlStrategy.CONTINUE, Set.of(), null))
                .add(Arguments.of(TransformationControlStrategy.SKIP_REST, Set.of(), null))
                .add(Arguments.of(TransformationControlStrategy.ABORT, null, null))
                .add(Arguments.of(TransformationControlStrategy.CONTINUE, null, null))
                .add(Arguments.of(TransformationControlStrategy.SKIP_REST, null, null))
                .build();
    }

    private static Set<ValidationMessage> notEmptySet() {
        return Set.of(mock(ValidationMessage.class), mock(ValidationMessage.class));
    }

    @ParameterizedTest
    @MethodSource("inputSupplier")
    void testAcceptShouldThrowExceptionWhenViolationsFoundAndNotIgnored(final TransformationControlStrategy underTest,
                                                                        final Set<ValidationMessage> violations,
                                                                        final Class<Exception> expected) {
        //given

        //when + then
        if (expected != null) {
            Assertions.assertThrows(expected, () -> underTest.accept(violations));
        } else {
            Assertions.assertDoesNotThrow(() -> underTest.accept(violations));
        }
    }
}
