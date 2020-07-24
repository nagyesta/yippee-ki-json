package com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaObject;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSimpleType;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.BaseCommonType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Definition used for object types with pre-defined properties in our JSON Schema.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"$comment", "description", "type", "properties", "required",
        "minProperties", "maxProperties", "additionalProperties"})
public final class JsonPropertiesSchemaTypeDefinition extends BaseCommonType {

    private final Map<String, JsonSchemaObject> properties;
    private final Boolean additionalProperties;
    private final Set<String> required;
    private final Integer minProperties;
    private final Integer maxProperties;

    private JsonPropertiesSchemaTypeDefinition(@NotNull final JsonPropertiesSchemaTypeDefinitionBuilder builder) {
        super(builder);
        this.properties = Collections.unmodifiableMap(new LinkedHashMap<>(builder.properties));
        if (CollectionUtils.isEmpty(builder.required)) {
            this.required = null;
        } else {
            this.required = Collections.unmodifiableSortedSet(new TreeSet<>(builder.required));
        }
        this.additionalProperties = builder.additionalProperties;
        this.minProperties = builder.minProperties;
        this.maxProperties = builder.maxProperties;
    }

    public static JsonPropertiesSchemaTypeDefinitionBuilder builder() {
        return new JsonPropertiesSchemaTypeDefinitionBuilder();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class JsonPropertiesSchemaTypeDefinitionBuilder
            extends BaseCommonTypeBuilder<JsonPropertiesSchemaTypeDefinitionBuilder> {
        private Map<String, JsonSchemaObject> properties;
        private Boolean additionalProperties;
        private Set<String> required;
        private Integer minProperties;
        private Integer maxProperties;

        private JsonPropertiesSchemaTypeDefinitionBuilder() {
            reset();
        }

        protected void reset() {
            super.reset();
            this.type(JsonSimpleType.OBJECT);
            this.properties = new LinkedHashMap<>();
            this.required = new TreeSet<>();
            this.additionalProperties = null;
            this.minProperties = null;
            this.maxProperties = null;
        }

        public JsonPropertiesSchemaTypeDefinitionBuilder addProperty(final boolean required,
                                                                     @NotNull final String key,
                                                                     @NotNull final JsonSchemaObject value) {
            if (required) {
                this.addRequiredProperty(key, value);
            } else {
                this.addProperty(key, value);
            }
            return getThis();
        }

        public JsonPropertiesSchemaTypeDefinitionBuilder addProperty(@NotNull final String key,
                                                                     @NotNull final JsonSchemaObject value) {
            this.properties.put(key, value);
            return getThis();
        }

        public JsonPropertiesSchemaTypeDefinitionBuilder addRequiredProperty(@NotNull final String key,
                                                                             @NotNull final JsonSchemaObject value) {
            this.addProperty(key, value);
            this.required.add(key);
            return getThis();
        }

        public JsonPropertiesSchemaTypeDefinitionBuilder disallowAdditionalProperties() {
            this.additionalProperties = false;
            return getThis();
        }

        public JsonPropertiesSchemaTypeDefinitionBuilder propertyCounts(@Nullable final Integer minProperties,
                                                                        @Nullable final Integer maxProperties) {
            this.minProperties = minProperties;
            this.maxProperties = maxProperties;
            return getThis();
        }

        @Override
        public JsonPropertiesSchemaTypeDefinitionBuilder type(@Nullable final JsonSimpleType ignored) {
            super.type(JsonSimpleType.OBJECT);
            return getThis();
        }

        public JsonPropertiesSchemaTypeDefinition build() {
            JsonPropertiesSchemaTypeDefinition value = new JsonPropertiesSchemaTypeDefinition(this);
            this.reset();
            return value;
        }

        protected JsonPropertiesSchemaTypeDefinitionBuilder getThis() {
            return this;
        }
    }
}
