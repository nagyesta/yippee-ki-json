package com.github.nagyesta.yippeekijson.metadata.schema.entity;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public final class ComponentContext {

    private final Class<?> javaType;
    private final String jsonTypeName;
    private final String componentName;
    private final ComponentType componentType;
    private final Map<String, PropertyContext> properties = new LinkedHashMap<>();
    private final Integer minProperties;
    private final Integer maxProperties;
    private String pathRestrictionValue;
    private String pathRestrictionDocs;
    private final DocumentationContext documentation;

    private ComponentContext(final ComponentContextBuilder builder) {
        this.javaType = builder.javaType;
        this.jsonTypeName = builder.jsonTypeName;
        this.componentName = builder.componentName;
        this.componentType = builder.componentType;
        this.documentation = builder.documentation;
        this.properties.putAll(builder.properties);
        this.pathRestrictionValue = builder.pathRestrictionValue;
        this.pathRestrictionDocs = builder.pathRestrictionDocs;
        this.maxProperties = builder.maxProperties;
        this.minProperties = builder.minProperties;
    }

    public static ComponentContextBuilder builder() {
        return new ComponentContextBuilder();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static class ComponentContextBuilder {
        private static final String NAME = "name";
        private Class<?> javaType;
        private String jsonTypeName;
        private String componentName;
        private ComponentType componentType;
        private DocumentationContext documentation;
        private Map<String, PropertyContext> properties;
        private String pathRestrictionValue;
        private String pathRestrictionDocs;
        private Integer minProperties;
        private Integer maxProperties;

        ComponentContextBuilder() {
            reset();
        }

        private void reset() {
            this.javaType = null;
            this.jsonTypeName = null;
            this.componentName = null;
            this.componentType = null;
            this.documentation = null;
            this.minProperties = null;
            this.maxProperties = null;
            this.properties = new LinkedHashMap<>();
            this.pathRestrictionValue = null;
            this.pathRestrictionDocs = null;
        }

        public ComponentContextBuilder jsonTypeName(@NotNull final String jsonTypeName) {
            if (this.jsonTypeName == null) {
                this.jsonTypeName = jsonTypeName;
            }
            return this;
        }

        public ComponentContextBuilder componentName(@NotNull final String componentName) {
            this.componentName = componentName;
            return this;
        }

        public ComponentContextBuilder componentType(@NotNull final ComponentType componentType) {
            this.componentType = componentType;
            return this;
        }

        public ComponentContextBuilder documentation(@NotNull final DocumentationContext documentation) {
            this.documentation = documentation;
            return this;
        }

        public ComponentContextBuilder allowedProperties(@Nullable final Integer min,
                                                         @Nullable final Integer max) {
            this.minProperties = normalizedPropertyCount(min);
            this.maxProperties = normalizedPropertyCount(max);
            return this;
        }

        private @Nullable Integer normalizedPropertyCount(@Nullable final Integer count) {
            if (count == null || count < 0) {
                return null;
            } else {
                return count;
            }
        }

        public ComponentContextBuilder javaType(@NotNull final Class<?> javaType) {
            this.javaType = javaType;
            return this;
        }

        public ComponentContextBuilder pathRestriction(@NotNull final String value,
                                                       @NotNull final String docs) {
            this.pathRestrictionValue = value;
            this.pathRestrictionDocs = docs;
            return this;
        }

        public ComponentContextBuilder properties(@NotNull final String name,
                                                  @NotNull final PropertyContext context) {
            Assert.isTrue(!this.properties.containsKey(name), "Property is already added: " + name);
            this.properties.put(name, context);
            return this;
        }

        public ComponentContextBuilder propertiesMerge(@NotNull final String[] names,
                                                       @NotNull final PropertyContext context) {
            String topLevel = names[0];
            String joined = String.join(".", names);
            if (this.properties.containsKey(joined)) {
                final PropertyContext existing = this.properties.get(joined);
                PropertyContext merged = existing.mergeWith(context);
                this.properties.put(joined, merged);
            } else if (this.properties.containsKey(topLevel)) {
                final PropertyContext existing = this.properties.remove(topLevel);
                PropertyContext merged = existing.mergeWith(context);
                this.properties.put(joined, merged);
            } else {
                this.properties(joined, context);
            }
            return this;
        }

        /**
         * Calls the constructor.
         *
         * @return new {@link ComponentContext}
         */
        public ComponentContext build() {
            final ComponentContext context = new ComponentContext(this);
            this.reset();
            return context;
        }
    }
}
