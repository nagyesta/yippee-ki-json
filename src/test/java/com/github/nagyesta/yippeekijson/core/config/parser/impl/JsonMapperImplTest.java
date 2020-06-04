package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.mapper.MappingException;
import org.jetbrains.annotations.TestOnly;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

class JsonMapperImplTest {

    private static final String KEY = "key";
    private static final String VALUE = "value";

    private static Object getObjectWithKeyField() {
        return new Object() {
            @TestOnly
            public String getKey() {
                return VALUE;
            }
        };
    }

    private static Stream<Arguments> mapToMapProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(new Object(), Map.of()))
                .add(Arguments.of(Map.<String, Object>of(KEY, VALUE), Map.<String, Object>of(KEY, VALUE)))
                .add(Arguments.of(getObjectWithKeyField(), Map.<String, Object>of(KEY, VALUE)))
                .build();
    }

    private static Stream<Arguments> invalidProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null, IllegalArgumentException.class))
                .add(Arguments.of(null, JsonMapper.MapTypeRef.INSTANCE, IllegalArgumentException.class))
                .add(Arguments.of(new Object(), null, IllegalArgumentException.class))
                .add(Arguments.of(KEY, JsonMapper.MapTypeRef.INSTANCE, MappingException.class))
                .build();
    }

    @ParameterizedTest
    @MethodSource("mapToMapProvider")
    void testMapToShouldMapValidInputToMap(final Object input, final Map<String, Object> expected) {
        //given
        final JsonMapper underTest = new JsonMapperImpl();

        //when
        final Map<String, Object> actual = underTest.mapTo(input, JsonMapper.MapTypeRef.INSTANCE);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("invalidProvider")
    void testMapToShouldMapValidInputToMap(final Object input, final TypeRef<?> typeRef, final Class<? extends Exception> expected) {
        //given
        final JsonMapper underTest = new JsonMapperImpl();

        //when + then exception
        Assertions.assertThrows(expected, () -> underTest.mapTo(input, typeRef));
    }
}
