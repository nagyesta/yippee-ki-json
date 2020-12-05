package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.JsonMapperImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

@LaunchAbortArmed
class StaticJsonSupplierTest {

    private static final String NONE = "{}";
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String KEY_VALUE = "{\"key\":\"value\"}";
    private static final Map<String, Object> MAP = Map.of(KEY, VALUE);
    private static final String INVALID = "{";

    private static Stream<Arguments> invalidProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(KEY, null))
                .add(Arguments.of(KEY_VALUE, null))
                .add(Arguments.of(null, mock(JsonMapper.class)))
                .add(Arguments.of(INVALID, new JsonMapperImpl()))
                .build();
    }

    private static Stream<Arguments> jsonProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(NONE, Map.of()))
                .add(Arguments.of(KEY_VALUE, MAP))
                .build();
    }

    @ParameterizedTest
    @MethodSource("jsonProvider")
    void testGetShouldReturnTheStaticJson(final String input, final Object expected) {
        //given
        JsonMapper jsonMapper = new JsonMapperImpl();
        final StaticJsonSupplier underTest = new StaticJsonSupplier(input, jsonMapper);

        //when
        final Object actual = underTest.get();

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("invalidProvider")
    void testConstructorShouldNotAllowNullsOrNonJsonValues(final String json, final JsonMapper jsonMapper) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StaticJsonSupplier(json, jsonMapper));
    }

    @ParameterizedTest
    @ValueSource(strings = {KEY_VALUE, NONE})
    void testToStringShouldContainClassNameAndKey(final String key) {
        //given
        JsonMapper jsonMapper = new JsonMapperImpl();
        final StaticJsonSupplier underTest = new StaticJsonSupplier(key, jsonMapper);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(StaticJsonSupplier.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(key));
    }
}
