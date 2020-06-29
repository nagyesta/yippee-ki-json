package com.github.nagyesta.yippeekijson.core.predicate.helper;

import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

class CombiningPredicateSupportTest {

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(mock(FunctionRegistry.class), null))
                .add(Arguments.of(null, List.of()))
                .build();
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldNotAllowNulls(final FunctionRegistry functionRegistry,
                                            final List<Map<String, RawConfigParam>> config) {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CombiningPredicateSupport(config, functionRegistry) {
            @Override
            public boolean test(final Object o) {
                return false;
            }
        });
    }
}
