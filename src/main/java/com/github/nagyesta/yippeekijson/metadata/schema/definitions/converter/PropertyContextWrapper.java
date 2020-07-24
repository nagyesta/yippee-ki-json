package com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter;

import com.github.nagyesta.yippeekijson.core.NamedComponentUtil;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.PropertyContext;
import lombok.Getter;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Wrapper for either a single {@link PropertyContext} instance or a {@link Map} of them representing
 * a level of nested properties.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Getter
public final class PropertyContextWrapper {

    private final String name;
    private final boolean collection;
    private final Optional<Map<String, PropertyContextWrapper>> children;
    private final Optional<PropertyContext> wrapped;

    private PropertyContextWrapper(@NotNull final PropertyContextWrapperBuilder builder) {
        this.name = builder.name;
        if (CollectionUtils.isEmpty(builder.children)) {
            this.collection = TypeUtils.isAssignable(builder.wrapped.getType(), Collection.class);
            if (this.collection) {
                final Type type = NamedComponentUtil.collectionTypeParameterOf(builder.wrapped.getType());
                this.wrapped = Optional.of(PropertyContext.builder()
                        .name(builder.wrapped.getName())
                        .required(builder.wrapped.isRequired())
                        .type(type)
                        .commonTypeRef(builder.wrapped.getCommonTypeRef())
                        .docs(builder.wrapped.getDocs())
                        .build());
            } else {
                this.wrapped = Optional.of(builder.wrapped);
            }
            this.children = Optional.empty();
        } else {
            this.collection = false;
            this.wrapped = Optional.empty();
            Map<String, PropertyContextWrapper> map = new LinkedHashMap<>();
            builder.children.forEach((key, value) -> map.put(key, value.build()));
            this.children = Optional.of(Collections.unmodifiableMap(map));
        }
    }

    public static PropertyContextWrapperBuilder builder() {
        return new PropertyContextWrapperBuilder();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static class PropertyContextWrapperBuilder {
        private String name;
        private Map<String, PropertyContextWrapperBuilder> children;
        private PropertyContext wrapped;

        PropertyContextWrapperBuilder() {
            reset();
        }

        private void reset() {
            this.name = null;
            this.wrapped = null;
            this.children = new LinkedHashMap<>();
        }

        public PropertyContextWrapperBuilder merge(@NotNull final PropertyContext propertyContext) {
            if (propertyContext.getChild().isEmpty()) {
                this.wrapped = propertyContext;
            } else {
                final PropertyContext childPropertyContext = propertyContext.getChild().get();
                final PropertyContextWrapperBuilder child = this.children.computeIfAbsent(childPropertyContext.getName(),
                        n -> PropertyContextWrapper.builder().merge(childPropertyContext));
                child.name(childPropertyContext.getName());
                child.merge(childPropertyContext);
            }
            return this;
        }

        public PropertyContextWrapperBuilder name(@NotNull final String name) {
            this.name = name;
            return this;
        }

        public PropertyContextWrapper build() {
            final PropertyContextWrapper value = new PropertyContextWrapper(this);
            reset();
            return value;
        }
    }
}
