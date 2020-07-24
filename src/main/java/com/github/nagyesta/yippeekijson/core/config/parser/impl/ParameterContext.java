package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.NamedComponentUtil;
import com.github.nagyesta.yippeekijson.core.annotation.EmbedParam;
import com.github.nagyesta.yippeekijson.core.annotation.MapParam;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.inject.Named;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
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
    private final String docs;
    private final Class<?> rawType;
    private final String pattern;
    private final Type resolvedType;

    private ParameterContext(final ParameterContextBuilder builder) {
        this.useCase = builder.useCase;
        this.name = builder.name;
        this.nullable = builder.nullable;
        this.collectionTyped = builder.collectionTyped;
        this.docs = builder.docs;
        this.rawType = builder.rawType;
        this.pattern = builder.pattern;
        this.resolvedType = builder.resolvedType;
    }

    public static ParameterContext forParameter(@NotNull final Parameter parameter) {

        ParameterContextBuilder builder = ParameterContext.builder()
                .collectionTyped(NamedComponentUtil.isCollectionTyped(parameter.getType()));
        if (parameter.isAnnotationPresent(EmbedParam.class)) {
            final EmbedParam annotation = parameter.getAnnotation(EmbedParam.class);
            convertEmbedParam(parameter, builder, annotation);
        } else if (parameter.isAnnotationPresent(MapParam.class)) {
            final MapParam annotation = parameter.getAnnotation(MapParam.class);
            convertMapParam(parameter, builder, annotation);
        } else if (parameter.isAnnotationPresent(ValueParam.class)) {
            final ValueParam annotation = parameter.getAnnotation(ValueParam.class);
            convertValueParam(parameter, builder, annotation);
        } else {
            throw new UnsupportedOperationException("Parameter is not annotated.");
        }
        return builder.build();
    }

    private static void convertValueParam(@NotNull final Parameter parameter,
                                          final ParameterContextBuilder builder,
                                          final ValueParam annotation) {
        String pattern = null;
        if (parameter.isAnnotationPresent(Pattern.class)) {
            pattern = parameter.getAnnotation(Pattern.class).regexp();
        }
        builder.name(findName(parameter, annotation.value()))
                .nullable(isNullable(parameter, annotation.nullable()))
                .rawType(NamedComponentUtil.resolveRawParamItemType(parameter, annotation.itemType()))
                .resolvedType(NamedComponentUtil.resolveParamType(parameter, annotation.itemType(), new Class[0]))
                .useCase(UseCase.forAnnotation(annotation))
                .pattern(pattern)
                .docs(annotation.docs());
    }

    private static void convertMapParam(@NotNull final Parameter parameter,
                                        final ParameterContextBuilder builder,
                                        final MapParam annotation) {
        builder.name(findName(parameter, annotation.value()))
                .nullable(isNullable(parameter, annotation.nullable()))
                .resolvedType(NamedComponentUtil.resolveParamType(parameter, Map.class, new Class[]{String.class, String.class}))
                .rawType(Map.class)
                .useCase(UseCase.forAnnotation(annotation))
                .docs(annotation.docs());
    }

    private static void convertEmbedParam(@NotNull final Parameter parameter,
                                          final ParameterContextBuilder builder,
                                          final EmbedParam annotation) {
        builder.name(findName(parameter, annotation.value()))
                .nullable(isNullable(parameter, annotation.nullable()))
                .resolvedType(NamedComponentUtil.resolveParamType(parameter, annotation.itemType(), annotation.itemTypeParams()))
                .rawType(NamedComponentUtil.resolveRawParamItemType(parameter, annotation.itemType()))
                .useCase(UseCase.forAnnotation(annotation))
                .docs(annotation.docs());
    }


    public static boolean supports(@NotNull final Parameter parameter) {
        return parameter.isAnnotationPresent(ValueParam.class)
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

    private static ParameterContextBuilder builder() {
        return new ParameterContextBuilder();
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

        public <T> T apply(@NotNull final RawConfigParam param, final boolean collectionNeeded, final Class<T> targetType) {
            if (collectionNeeded) {
                return targetType.cast(collectionFunction.apply(param));
            } else {
                return targetType.cast(singularFunction.apply(param));
            }
        }
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class ParameterContextBuilder {
        private String name;
        private boolean nullable;
        private boolean collectionTyped;
        private UseCase useCase;
        private String docs;
        private String pattern;
        private Class<?> rawType;
        private Type resolvedType;

        private ParameterContextBuilder() {
        }

        public ParameterContextBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public ParameterContextBuilder nullable(final boolean nullable) {
            this.nullable = nullable;
            return this;
        }

        public ParameterContextBuilder collectionTyped(final boolean collectionTyped) {
            this.collectionTyped = collectionTyped;
            return this;
        }

        public ParameterContextBuilder useCase(final UseCase useCase) {
            this.useCase = useCase;
            return this;
        }

        public ParameterContextBuilder docs(final String docs) {
            this.docs = docs;
            return this;
        }

        public ParameterContextBuilder pattern(final String pattern) {
            this.pattern = pattern;
            return this;
        }

        public ParameterContextBuilder rawType(final Class<?> rawType) {
            this.rawType = rawType;
            return this;
        }

        public ParameterContextBuilder resolvedType(final Type resolvedType) {
            this.resolvedType = resolvedType;
            return this;
        }

        public ParameterContext build() {
            return new ParameterContext(this);
        }
    }
}
