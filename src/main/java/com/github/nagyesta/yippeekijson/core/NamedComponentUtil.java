package com.github.nagyesta.yippeekijson.core;

import com.github.nagyesta.yippeekijson.metadata.schema.entity.typehelper.ParametrizedTypeAware;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Utility class for dealing with named components.
 */
public final class NamedComponentUtil {

    private static final Function<Type, Type> PARAMETRIZED_MAP_TO_MAP = t -> {
        if (TypeUtils.isAssignable(Map.class, t)) {
            return Map.class;
        } else {
            return t;
        }
    };

    private NamedComponentUtil() {
    }

    /**
     * Finds a constructor annotated with a given annotation.
     *
     * @param sourceClass The class declaring the constructor
     * @param annotation  The annotation we need
     * @param <F>         The type of the annotation
     * @param <T>         The type of the class
     * @return An {@link Optional} containing the constructor is found.
     */
    @NotNull
    public static <F, T extends Annotation> Optional<Constructor<?>> findAnnotatedConstructorOfNamedComponent(
            @NotNull final Class<? extends F> sourceClass,
            @NotNull final Class<T> annotation) {
        return Arrays.stream(sourceClass.getDeclaredConstructors())
                .filter(c -> c.isAnnotationPresent(annotation))
                .findFirst();
    }

    /**
     * Returns true if the provided type is {@link Collection} or a subclass of {@link Collection}.
     *
     * @param type The type we want ot examine
     * @return true if the {@link Collection} is assignable from type
     */
    public static boolean isCollectionTyped(@NotNull final Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    /**
     * Determines the parameter type of the provided parameter allowing clarifications/overrides to be made
     * using the other two types we receive (assuming that those are the annotated values).
     *
     * @param parameter              The the parameter we are examining
     * @param typeOverride           The item type specified as an override similarly to {@link #resolveRawParamItemType(Parameter, Class)}
     * @param typeOverrideTypeParams The type parameters of the override to allow parametrized type usage
     * @return The resolved type which is guaranteed to be assignable to the parameter type.
     */
    @NotNull
    public static Type resolveParamType(@NotNull final Parameter parameter,
                                        @Nullable final Class<?> typeOverride,
                                        @NotNull final Class<?>[] typeOverrideTypeParams) {
        Type resolvedType;
        if (typeOverride != null && !typeOverride.equals(Void.class)) {
            resolvedType = toType(typeOverride, typeOverrideTypeParams);
        } else {
            resolvedType = itemTypeFromParameter(parameter);
        }
        return wrapIfCollectionAndTest(parameter, resolvedType);
    }

    /**
     * Resolves the raw parameter item type of a parameter based on the parameter and the type provided.
     * This means that in case of {@link Collection} typed parameters we return the type parameter
     * used for the items in the collection, otherwise we rely on the type parameter of this method
     * and as a last resort the actual type of the parameter.
     *
     * @param parameter    The parameter for which we want to find the logical item type
     * @param typeOverride The type hint we received from the param annotation
     * @return item type
     */
    @Nullable
    public static Class<?> resolveRawParamItemType(@NotNull final Parameter parameter,
                                                   @Nullable final Class<?> typeOverride) {
        final boolean isCollection = isCollectionTyped(parameter.getType());
        Class<?> resolvedType = parameter.getType();
        if (typeOverride != null && !typeOverride.equals(Void.class)) {
            resolvedType = typeOverride;
        } else if (isCollection) {
            resolvedType = resolveItemType(parameter)
                    .map(PARAMETRIZED_MAP_TO_MAP)
                    .filter(Class.class::isInstance)
                    .map(Class.class::cast)
                    .orElseThrow(() -> new IllegalArgumentException("Collection type has no type parameter."));
        }
        return resolvedType;
    }

    @NotNull
    private static Type wrapIfCollectionAndTest(@NotNull final Parameter parameter,
                                                final Type resolvedType) {
        Type result = resolvedType;
        if (isCollectionTyped(parameter.getType())) {
            result = TypeUtils.parameterize(Collection.class, result);
        }
        Assert.isTrue(TypeUtils.isAssignable(result, parameter.getType()), "Resolved type: " + result.getTypeName()
                + " is not assignable to: " + parameter.getType());
        return result;
    }

    @NotNull
    private static Type itemTypeFromParameter(@NotNull final Parameter parameter) {
        Type resolvedType;
        if (isCollectionTyped(parameter.getType())) {
            resolvedType = resolveItemType(parameter)
                    .orElseThrow(() -> new IllegalArgumentException("Collection type has no type parameter."));
        } else {
            resolvedType = parameter.getType();
        }
        return resolvedType;
    }

    @NotNull
    private static Type toType(@NotNull final Class<?> typeOverride,
                               @NotNull final Class<?>[] typeOverrideTypeParams) {
        Type resolvedType;
        if (ArrayUtils.isEmpty(typeOverrideTypeParams)) {
            resolvedType = typeOverride;
        } else {
            resolvedType = TypeUtils.parameterize(typeOverride, typeOverrideTypeParams);
        }
        return resolvedType;
    }

    @NotNull
    private static Optional<Type> resolveItemType(@NotNull final Parameter parameter) {
        return TypeUtils.getTypeArguments(parameter.getParameterizedType(), Collection.class)
                .values()
                .stream()
                .findFirst();
    }

    @NotNull
    public static Class<?> asRawClass(@NotNull final Type target) {
        Class<?> targetClass = Object.class;
        if (target instanceof Class) {
            targetClass = (Class<?>) target;
        } else if (target instanceof ParameterizedType) {
            final Type rawType = ((ParameterizedType) target).getRawType();
            if (rawType instanceof Class) {
                targetClass = (Class<?>) rawType;
            }
        }
        return targetClass;
    }

    @NotNull
    public static Type collectionTypeParameterOf(@NotNull final Type type) {
        return getSingleTypeParameterOf(type, Collection.class);
    }

    @NotNull
    public static Type supplierTypeParameterOf(@NotNull final Type type) {
        return getSingleTypeParameterOf(type, Supplier.class);
    }

    @NotNull
    public static Type functionInputTypeParameterOf(@NotNull final Type type) {
        return getSingleTypeParameterOf(type, Function.class);
    }

    @NotNull
    public static Type functionOutputTypeParameterOf(@NotNull final Type type) {
        return TypeUtils.getTypeArguments(type, Function.class)
                .values().stream().skip(1).findFirst().orElse(Object.class);
    }

    @NotNull
    public static Type predicateTypeParameterOf(@NotNull final Type type) {
        return getSingleTypeParameterOf(type, Predicate.class);
    }

    @NotNull
    private static Type getSingleTypeParameterOf(@NotNull final Type type, @NotNull final Class<?> genericClass) {
        return TypeUtils.getTypeArguments(type, genericClass)
                .values().stream().findFirst().orElse(Object.class);
    }

    @NotNull
    public static Type translateKnownType(@NotNull final Type typeParameter) {
        return ParametrizedTypeAware.TYPE_TRANSLATION_PRECEDENCE.stream()
                .filter(type -> TypeUtils.isAssignable(typeParameter, type))
                .findFirst().orElse(Object.class);
    }
}
