package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.JsonMapperImpl;
import com.github.nagyesta.yippeekijson.core.exception.AbortTransformationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

@LaunchAbortArmed
class JsonParseFunctionTest {

    private static final String EMPTY = "{}";
    private static final String KEY_42 = "{\"key\": 42}";
    private static final String KEY = "key";
    private static final BigInteger INT_42 = new BigInteger("42");
    private static final String INVALID = "{";

    private static Stream<Arguments> invalidInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, AbortTransformationException.class))
                .add(Arguments.of(INVALID, AbortTransformationException.class))
                .build();
    }

    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(EMPTY, Map.of()))
                .add(Arguments.of(KEY_42, Map.of(KEY, INT_42)))
                .build();
    }

    @Test
    void testConstructorShouldThrowExceptionForNull() {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new JsonParseFunction(null));
    }

    @ParameterizedTest
    @MethodSource("invalidInputProvider")
    void testApplyShouldThrowExceptionForInvalidData(final String input, final Class<Exception> expected) {
        //given
        JsonMapper jsonMapper = new JsonMapperImpl();
        final JsonParseFunction underTest = new JsonParseFunction(jsonMapper);

        //when + then exception
        Assertions.assertThrows(expected, () -> underTest.apply(input));
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testApplyShouldParseValidData(final String input, final Map<?, ?> expected) {
        //given
        JsonMapper jsonMapper = new JsonMapperImpl();
        final JsonParseFunction underTest = new JsonParseFunction(jsonMapper);

        //when
        final Object actual = underTest.apply(input);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testToStringShouldContainClassName() {
        //given
        JsonMapper jsonMapper = mock(JsonMapper.class);
        final JsonParseFunction underTest = new JsonParseFunction(jsonMapper);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(JsonParseFunction.class.getSimpleName()));
    }
}
