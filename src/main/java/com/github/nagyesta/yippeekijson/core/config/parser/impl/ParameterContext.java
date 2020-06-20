package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.annotation.EmbedParam;
import com.github.nagyesta.yippeekijson.core.annotation.MapParam;
import com.github.nagyesta.yippeekijson.core.annotation.MethodParam;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

/**
 * Contains all information about a parameter needed for instantiation.
 */
@Getter
@ToString
public final class ParameterContext {

    private final String name;
    private final boolean nullable;
    private final boolean collectionTyped;
    private final UseCase useCase;

    private ParameterContext(final UseCase useCase, final String name, final boolean nullable, final boolean collectionTyped) {
        this.useCase = useCase;
        this.name = name;
        this.nullable = nullable;
        this.collectionTyped = collectionTyped;
    }

    @SuppressWarnings("deprecation")
    public static ParameterContext forParameter(@NotNull final Parameter parameter) {
        ParameterContext result;
        if (parameter.isAnnotationPresent(EmbedParam.class)) {
            final EmbedParam annotation = parameter.getAnnotation(EmbedParam.class);
            result = process(parameter, UseCase.forAnnotation(annotation), annotation.value(), annotation.nullable());
        } else if (parameter.isAnnotationPresent(MapParam.class)) {
            final MapParam annotation = parameter.getAnnotation(MapParam.class);
            result = process(parameter, UseCase.forAnnotation(annotation), annotation.value(), annotation.nullable());
        } else if (parameter.isAnnotationPresent(ValueParam.class)) {
            final ValueParam annotation = parameter.getAnnotation(ValueParam.class);
            result = process(parameter, UseCase.forAnnotation(annotation), annotation.value(), annotation.nullable());
        } else {
            final MethodParam annotation = parameter.getAnnotation(MethodParam.class);
            result = new ParameterContext(UseCase.forAnnotation(annotation),
                    annotation.value(), annotation.nullable(), annotation.repeat());
        }
        return result;
    }

    @NotNull
    private static ParameterContext process(@NotNull final Parameter parameter,
                                            @NotNull final UseCase useCase,
                                            @NotNull final String value,
                                            final boolean nullable) {
        return new ParameterContext(useCase,
                findName(parameter, value),
                isNullable(parameter, nullable),
                Collection.class.isAssignableFrom(parameter.getType()));
    }

    @SuppressWarnings("deprecation")
    public static boolean supports(@NotNull final Parameter parameter) {
        return parameter.isAnnotationPresent(MethodParam.class)
                || parameter.isAnnotationPresent(ValueParam.class)
                || parameter.isAnnotationPresent(MapParam.class)
                || parameter.isAnnotationPresent(EmbedParam.class);
    }

    private static String findName(@NotNull final Parameter parameter,
                                   @NotNull final String annotationValue) {
        String paramName = StringUtils.trimToNull(annotationValue);
        if (paramName == null) {
            paramName = processAnnotation(parameter, Qualifier.class, Qualifier::value);
        }
        if (paramName == null) {
            paramName = processAnnotation(parameter, Named.class, Named::value);
        }
        return Objects.requireNonNullElse(paramName, parameter.getName());
    }

    private static boolean isNullable(@NotNull final Parameter parameter,
                                      final boolean annotationValue) {
        return parameter.isAnnotationPresent(javax.annotation.Nullable.class)
                || parameter.isAnnotationPresent(org.springframework.lang.Nullable.class)
                || annotationValue;
    }

    @Nullable
    private static <A extends Annotation> String processAnnotation(@NotNull final Parameter parameter,
                                                                   @NotNull final Class<A> annotation,
                                                                   @NotNull final Function<A, String> nameFunction) {
        String paramName = null;
        if (parameter.isAnnotationPresent(annotation)) {
            paramName = StringUtils.trimToNull(nameFunction.apply(parameter.getAnnotation(annotation)));
        }
        return paramName;
    }

    public enum UseCase {
        /**
         * Represents the use case of {@link String} valued parameter(s).
         */
        VALUE(RawConfigParam::asString, RawConfigParam::asStrings),
        /**
         * Represents the use case of {@link String} valued {@link java.util.Map} parameter(s).
         */
        MAP(RawConfigParam::asStringMap, RawConfigParam::asStringMaps),
        /**
         * Represents the use case of {@link java.util.Map} parameter(s) meant to be embedded.
         */
        EMBEDDED(RawConfigParam::asMap, RawConfigParam::asMaps);

        private final Function<RawConfigParam, Object> singularFunction;
        private final Function<RawConfigParam, Collection<?>> collectionFunction;

        UseCase(final Function<RawConfigParam, Object> singularFunction,
                final Function<RawConfigParam, Collection<?>> collectionFunction) {
            this.singularFunction = singularFunction;
            this.collectionFunction = collectionFunction;
        }

        @SuppressWarnings("deprecation")
        static UseCase forAnnotation(final MethodParam methodParam) {
            if (methodParam.stringMap() && methodParam.paramMap()) {
                return EMBEDDED;
            } else if (methodParam.stringMap()) {
                return MAP;
            } else {
                Assert.isTrue(!methodParam.paramMap(), "Param map cannot be active if string map isn't.");
                return VALUE;
            }
        }

        @SuppressWarnings("unused")
        static UseCase forAnnotation(final MapParam methodParam) {
            return MAP;
        }

        @SuppressWarnings("unused")
        static UseCase forAnnotation(final EmbedParam methodParam) {
            return EMBEDDED;
        }

        @SuppressWarnings("unused")
        static UseCase forAnnotation(final ValueParam methodParam) {
            return VALUE;
        }

        public Object apply(@NotNull final RawConfigParam param, final boolean collectionNeeded) {
            if (collectionNeeded) {
                return collectionFunction.apply(param);
            } else {
                return singularFunction.apply(param);
            }
        }
    }
}
