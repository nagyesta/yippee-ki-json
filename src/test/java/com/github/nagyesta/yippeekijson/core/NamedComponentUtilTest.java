package com.github.nagyesta.yippeekijson.core;

import com.github.nagyesta.yippeekijson.core.config.parser.impl.ParameterContext;
import com.github.nagyesta.yippeekijson.core.test.params.ParamAnnotationHolder;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

class NamedComponentUtilTest {

    private static Stream<Arguments> paramTypeProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(HashMap.class, new Class[]{String.class, String.class},
                        "java.util.Collection<java.util.HashMap<java.lang.String, java.lang.String>>"))
                .add(Arguments.of(Void.class, null,
                        "java.util.Collection<java.util.Map<java.lang.String, java.lang.String>>"))
                .add(Arguments.of(null, null,
                        "java.util.Collection<java.util.Map<java.lang.String, java.lang.String>>"))
                .build();
    }

    private static Stream<Arguments> paramItemTypeProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(HashMap.class, HashMap.class))
                .add(Arguments.of(Void.class, Map.class))
                .add(Arguments.of(null, Map.class))
                .build();
    }

    private static Stream<Arguments> asRawClassProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(TypeUtils.parameterize(Collection.class, String.class), Collection.class))
                .add(Arguments.of(Map.class, Map.class))
                .build();
    }

    @ParameterizedTest
    @MethodSource("paramTypeProvider")
    void testResolveParamType(final Class<?> typeOverride,
                              final Class<?>[] typeOverrideParams,
                              final String expected) {
        //given
        final Parameter parameter = ParamAnnotationHolder
                .getParameterForUseCase(ParameterContext.UseCase.MAP, ParamAnnotationHolder.PARAM_NAMED_LIST);

        //when
        final Type actual = NamedComponentUtil.resolveParamType(parameter, typeOverride, typeOverrideParams);

        //then
        Assertions.assertEquals(expected, actual.getTypeName());
    }

    @ParameterizedTest
    @MethodSource("paramItemTypeProvider")
    void testResolveRawParamItemType(final Class<?> typeOverride,
                                     final Class<?> expected) {
        //given
        final Parameter parameter = ParamAnnotationHolder
                .getParameterForUseCase(ParameterContext.UseCase.MAP, ParamAnnotationHolder.PARAM_NAMED_LIST);

        //when
        final Type actual = NamedComponentUtil.resolveRawParamItemType(parameter, typeOverride);

        //then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("asRawClassProvider")
    void testAsRawClass(final Type input, final Class<?> expected) {
        //given

        //when
        final Type actual = NamedComponentUtil.asRawClass(input);

        //then
        Assertions.assertEquals(expected, actual);
    }
}
