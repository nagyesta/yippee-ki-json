package com.github.nagyesta.yippeekijson.metadata.schema.entity;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

/**
 * Value object to represent property specific information parsed from multiple sources.
 */
@Getter
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class PropertyContext {

    private final String name;

    private final Optional<String> docs;

    private final Optional<PropertyContext> child;

    private final Type type;

    private final boolean required;

    private final String pattern;

    private final Optional<String> commonTypeRef;

    private PropertyContext(@NotNull final PropertyContextBuilder builder) {
        this.name = builder.name;
        this.docs = builder.docs;
        this.child = builder.child;
        this.type = builder.type;
        this.required = builder.required;
        this.pattern = builder.pattern;
        this.commonTypeRef = builder.commonTypeRef;
    }

    public static PropertyContextBuilder builder() {
        return new PropertyContextBuilder();
    }

    /**
     * Merges the information represented by the current instance and the one provided as parameter.
     *
     * @param other the parameter we need to merge into the current object
     * @return a {@link PropertyContext} representing the merged information
     */
    public PropertyContext mergeWith(@Nullable final PropertyContext other) {
        if (other == null) {
            return this;
        }
        Optional<PropertyContext> mergedChild;
        if (this.child.isEmpty()) {
            mergedChild = other.child;
        } else {
            mergedChild = this.child.map(ch -> ch.mergeWith(other.child.orElse(null)));
        }
        return PropertyContext.builder()
                .child(mergedChild)
                .commonTypeRef(optionalFromValuesInOrder(other.commonTypeRef, this.commonTypeRef, null))
                .docs(optionalFromValuesInOrder(other.docs, this.docs, null))
                .name(Objects.requireNonNullElse(other.name, this.name))
                .required(other.required && this.required)
                .type(Objects.requireNonNullElse(other.type, this.type))
                .build();
    }

    @SuppressWarnings("SameParameterValue")
    @NotNull
    private <T> Optional<T> optionalFromValuesInOrder(@NotNull final Optional<T> primary,
                                                      @NotNull final Optional<T> secondary,
                                                      @Nullable final T fallback) {
        return Optional.ofNullable(primary.orElse(secondary.orElse(fallback)));
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static class PropertyContextBuilder {
        private String name;
        private Optional<String> docs;
        private Optional<PropertyContext> child;
        private Type type;
        private boolean required;
        private String pattern;
        private Optional<String> commonTypeRef;

        PropertyContextBuilder() {
            reset();
        }

        private void reset() {
            this.name = null;
            this.docs = Optional.empty();
            this.child = Optional.empty();
            this.type = null;
            this.required = true;
            this.pattern = null;
            this.commonTypeRef = Optional.empty();
        }

        public PropertyContextBuilder name(@NotNull final String name) {
            this.name = name;
            return this;
        }

        public PropertyContextBuilder docs(@NotNull final Optional<String> docs) {
            this.docs = docs;
            return this;
        }

        public PropertyContextBuilder child(@NotNull final Optional<PropertyContext> child) {
            this.child = child;
            return this;
        }

        public PropertyContextBuilder type(@Nullable final Type type) {
            this.type = type;
            return this;
        }

        public PropertyContextBuilder required(final boolean required) {
            this.required = required;
            return this;
        }

        public PropertyContextBuilder pattern(final String pattern) {
            this.pattern = pattern;
            return this;
        }

        public PropertyContextBuilder commonTypeRef(@NotNull final Optional<String> commonTypeRef) {
            this.commonTypeRef = commonTypeRef;
            return this;
        }

        public PropertyContext build() {
            final PropertyContext context = new PropertyContext(this);
            this.reset();
            return context;
        }
    }
}
