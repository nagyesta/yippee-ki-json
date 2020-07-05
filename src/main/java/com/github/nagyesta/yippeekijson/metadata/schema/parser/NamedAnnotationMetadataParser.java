package com.github.nagyesta.yippeekijson.metadata.schema.parser;

import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentType;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.function.Function;

/**
 * Parser class for metadata that can be deduced from the NamedRule, NamedFunction, etc. annotations.
 *
 * @param <T> The annotation class
 */
public class NamedAnnotationMetadataParser<T extends Annotation> {

    private static final String NAME = "name";
    private final ComponentType componentType;
    private final String namePrefix;
    private final Class<T> annotationType;
    private final Function<T, String> nameFunction;

    public NamedAnnotationMetadataParser(@NotNull final ComponentType componentType,
                                         @NotNull final Function<T, String> nameFunction,
                                         @NotNull final Class<T> annotationType) {
        this.componentType = componentType;
        this.nameFunction = nameFunction;
        this.namePrefix = componentType.name().toLowerCase();
        this.annotationType = annotationType;
    }

    /**
     * Merges the data represented by an annotation instance into the builder we provide.
     *
     * @param source      The source annotation
     * @param destination The builder we want to merge into
     * @return The builder
     */
    public ComponentContext.ComponentContextBuilder mergeInto(@NotNull final Annotation source,
                                                              @NotNull final ComponentContext.ComponentContextBuilder destination) {
        Assert.isInstanceOf(annotationType, source, "Annotation is not an instance of: " + annotationType.getSimpleName());
        final String componentName = nameFunction.apply(annotationType.cast(source));
        return destination.jsonTypeName(namePrefix + StringUtils.capitalize(componentName))
                .componentName(componentName)
                .componentType(componentType);
    }
}
