package com.github.nagyesta.yippeekijson.core.test.params;

import com.github.nagyesta.yippeekijson.core.annotation.EmbedParam;
import com.github.nagyesta.yippeekijson.core.annotation.MapParam;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.ParameterContext;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import jakarta.inject.Named;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("checkstyle:JavadocVariable")
public abstract class ParamAnnotationHolder {
    public static final String VALUE_PARAM = "valueParam";
    public static final String MAP_PARAM = "mapParam";
    public static final String EMBED_PARAM = "embedParam";
    public static final String NOTHING = "nothing";
    public static final String PARAM_DIFFERENT_NAME = "differentName";
    public static final String PARAM_DIFFERENT_NAME_EXPECTED = "name";
    public static final String PARAM_SAME_NAME = "sameName";
    public static final String PARAM_JAKARTA_NULLABLE = "jakartaNullable";
    public static final String PARAM_SPRING_NULLABLE = "springNullable";
    public static final String PARAM_SPRING_NULLABLE_EXPECTED = "nullable";
    public static final String PARAM_NAMED_LIST = "namedList";
    public static final String PARAM_NAMED_LIST_EXPECTED = "list";

    private ParamAnnotationHolder() {
        throw new IllegalStateException("Utility.");
    }

    private static void valueParam(@ValueParam("name") final String differentName,
                                   @ValueParam("sameName") final String sameName,
                                   @ValueParam @jakarta.annotation.Nullable final String jakartaNullable,
                                   @ValueParam @Qualifier("nullable") @Nullable final String springNullable,
                                   @ValueParam @Named("list") final Collection<String> namedList,
                                   final String nothing) {
    }

    private static void mapParam(@MapParam("name") final Map<String, String> differentName,
                                 @MapParam("sameName") final Map<String, String> sameName,
                                 @MapParam @jakarta.annotation.Nullable final Map<String, String> jakartaNullable,
                                 @MapParam @Qualifier("nullable") @Nullable final Map<String, String> springNullable,
                                 @MapParam @Named("list") final Collection<Map<String, String>> namedList,
                                 final Map<String, String> nothing) {
    }

    private static void embedParam(@EmbedParam("name") final Map<String, RawConfigParam> differentName,
                                   @EmbedParam("sameName") final Map<String, RawConfigParam> sameName,
                                   @EmbedParam @jakarta.annotation.Nullable final Map<String, RawConfigParam> jakartaNullable,
                                   @EmbedParam @Qualifier("nullable") @Nullable final Map<String, RawConfigParam> springNullable,
                                   @EmbedParam @Named("list") final Collection<Map<String, RawConfigParam>> namedList,
                                   final Map<String, RawConfigParam> nothing) {
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

    public static ParameterContext getParamContextForUseCaseUnchecked(final ParameterContext.UseCase useCase, final String name) {
        switch (useCase) {
            case VALUE:
                return getParameterContextForUnchecked(VALUE_PARAM, name);
            case MAP:
                return getParameterContextForUnchecked(MAP_PARAM, name);
            case EMBEDDED:
            default:
                return getParameterContextForUnchecked(EMBED_PARAM, name);
        }
    }

    public static Parameter getParameterForUseCase(final ParameterContext.UseCase useCase, final String name) {
        switch (useCase) {
            case VALUE:
                return getParameterFor(VALUE_PARAM, name)
                        .orElseThrow(IllegalArgumentException::new);
            case MAP:
                return getParameterFor(MAP_PARAM, name)
                        .orElseThrow(IllegalArgumentException::new);
            case EMBEDDED:
            default:
                return getParameterFor(EMBED_PARAM, name)
                        .orElseThrow(IllegalArgumentException::new);
        }
    }

    @NotNull
    private static ParameterContext getParameterContextFor(final String methodName, final String paramName) {
        return getParameterFor(methodName, paramName)
                .map(ParameterContext::forParameter)
                .orElseThrow(IllegalArgumentException::new);
    }

    private static Optional<Parameter> getParameterFor(final String methodName, final String paramName) {
        return Arrays.stream(ParamAnnotationHolder.class.getDeclaredMethods())
                .filter(method -> methodName.equals(method.getName()))
                .map(Method::getParameters)
                .flatMap(Arrays::stream)
                .filter(param -> paramName.equals(param.getName()))
                .filter(ParameterContext::supports)
                .findFirst();
    }

    @NotNull
    private static ParameterContext getParameterContextForUnchecked(final String methodName, final String paramName) {
        return Arrays.stream(ParamAnnotationHolder.class.getDeclaredMethods())
                .filter(method -> methodName.equals(method.getName()))
                .map(Method::getParameters)
                .flatMap(Arrays::stream)
                .filter(param -> paramName.equals(param.getName()))
                .map(ParameterContext::forParameter)
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
