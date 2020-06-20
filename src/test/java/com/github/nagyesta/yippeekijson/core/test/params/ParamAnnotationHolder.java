package com.github.nagyesta.yippeekijson.core.test.params;

import com.github.nagyesta.yippeekijson.core.annotation.EmbedParam;
import com.github.nagyesta.yippeekijson.core.annotation.MapParam;
import com.github.nagyesta.yippeekijson.core.annotation.MethodParam;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.ParameterContext;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;

import javax.inject.Named;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@SuppressWarnings("checkstyle:JavadocVariable")
public abstract class ParamAnnotationHolder {
    public static final String METHOD_PARAM = "methodParam";
    public static final String VALUE_PARAM = "valueParam";
    public static final String MAP_PARAM = "mapParam";
    public static final String EMBED_PARAM = "embedParam";
    public static final String METHOD_PARAM_ALL_FALSE = "allFalse";
    public static final String METHOD_PARAM_PARAM_MAP_TRUE = "paramMapTrue";
    public static final String METHOD_PARAM_PARAM_MAP_FALSE = "paramMapFalse";
    public static final String METHOD_PARAM_REPEAT_FALSE = "repeatFalse";
    public static final String METHOD_PARAM_REPEAT_TRUE = "repeatTrue";
    public static final String METHOD_PARAM_STRING_MAP_TRUE = "stringMapTrue";
    public static final String METHOD_PARAM_ALL_TRUE = "allTrue";
    public static final String PARAM_DIFFERENT_NAME = "differentName";
    public static final String PARAM_DIFFERENT_NAME_EXPECTED = "name";
    public static final String PARAM_SAME_NAME = "sameName";
    public static final String PARAM_JAVAX_NULLABLE = "javaxNullable";
    public static final String PARAM_SPRING_NULLABLE = "springNullable";
    public static final String PARAM_SPRING_NULLABLE_EXPECTED = "nullable";
    public static final String PARAM_NAMED_LIST = "namedList";
    public static final String PARAM_NAMED_LIST_EXPECTED = "list";
    private static final String VALUE = "value";

    private ParamAnnotationHolder() {
        throw new IllegalStateException("Utility.");
    }

    private static void valueParam(@ValueParam("name") final String differentName,
                                   @ValueParam("sameName") final String sameName,
                                   @ValueParam @javax.annotation.Nullable final String javaxNullable,
                                   @ValueParam @Qualifier("nullable") @Nullable final String springNullable,
                                   @ValueParam @Named("list") final Collection<String> namedList) {
    }

    private static void mapParam(@MapParam("name") final Map<String, String> differentName,
                                 @MapParam("sameName") final Map<String, String> sameName,
                                 @MapParam @javax.annotation.Nullable final Map<String, String> javaxNullable,
                                 @MapParam @Qualifier("nullable") @Nullable final Map<String, String> springNullable,
                                 @MapParam @Named("list") final Collection<Map<String, String>> namedList) {
    }

    private static void embedParam(@EmbedParam("name") final Map<String, RawConfigParam> differentName,
                                   @EmbedParam("sameName") final Map<String, RawConfigParam> sameName,
                                   @EmbedParam @javax.annotation.Nullable final Map<String, RawConfigParam> javaxNullable,
                                   @EmbedParam @Qualifier("nullable") @Nullable final Map<String, RawConfigParam> springNullable,
                                   @EmbedParam @Named("list") final Collection<Map<String, RawConfigParam>> namedList) {
    }

    @SuppressWarnings({"deprecation", "DefaultAnnotationParam"})
    static void methodParam(@MethodParam(value = VALUE, stringMap = false, paramMap = false, repeat = false)
                            @NotNull final Object allFalse,
                            @MethodParam(value = VALUE, stringMap = false, paramMap = false, repeat = true)
                            @NotNull final Object repeatTrue,
                            @MethodParam(value = VALUE, stringMap = false, paramMap = true, repeat = false)
                            @NotNull final Object paramMapTrue,
                            @MethodParam(value = VALUE, stringMap = true, paramMap = false, repeat = true)
                            @NotNull final Object paramMapFalse,
                            @MethodParam(value = VALUE, stringMap = true, paramMap = false, repeat = false)
                            @NotNull final Object stringMapTrue,
                            @MethodParam(value = VALUE, stringMap = true, paramMap = true, repeat = false)
                            @NotNull final Object repeatFalse,
                            @MethodParam(value = VALUE, stringMap = true, paramMap = true, repeat = true)
                            @NotNull final Object allTrue
    ) {

    }


    public static ParameterContext getParamContextForUseCase(final ParameterContext.UseCase useCase, final String name) {
        switch (useCase) {
            case VALUE:
                return getParameterContextFor(VALUE_PARAM, name);
            case MAP:
                return getParameterContextFor(MAP_PARAM, name);
            case EMBEDDED:
            default:
                return getParameterContextFor(EMBED_PARAM, name);
        }
    }

    public static ParameterContext getMethodParamContext(final String name) {
        return getParameterContextFor(METHOD_PARAM, name);
    }

    @NotNull
    private static ParameterContext getParameterContextFor(final String methodName, final String paramName) {
        return Arrays.stream(ParamAnnotationHolder.class.getDeclaredMethods())
                .filter(method -> methodName.equals(method.getName()))
                .map(Method::getParameters)
                .flatMap(Arrays::stream)
                .filter(param -> paramName.equals(param.getName()))
                .filter(ParameterContext::supports)
                .map(ParameterContext::forParameter)
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
