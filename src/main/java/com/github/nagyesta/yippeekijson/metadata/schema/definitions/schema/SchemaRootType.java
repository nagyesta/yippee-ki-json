package com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaObject;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSimpleType;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.BaseCommonType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Definition used for the root of our JSON Schema.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"$schema", "$comment", "definitions", "properties", "required",
        "minProperties", "maxProperties", "uniqueItems", "additionalProperties"})
public final class SchemaRootType extends BaseCommonType {

    @JsonProperty("$schema")
    private final String schemaRef;
    private final Map<String, JsonDefinitionsType> definitions;
    private final Map<String, JsonSchemaObject> properties;
    private final Boolean additionalProperties;
    private final Set<String> required;

    private SchemaRootType(@NotNull final SchemaRootType.SchemaRootTypeBuilder builder) {
        super(builder);
        this.schemaRef = builder.schemaRef;
        this.definitions = Collections.unmodifiableMap(new LinkedHashMap<>(builder.definitions));
        this.properties = Collections.unmodifiableMap(new LinkedHashMap<>(builder.properties));
        this.required = Collections.unmodifiableSortedSet(new TreeSet<>(builder.required));
        this.additionalProperties = builder.additionalProperties;

    }

    public static SchemaRootTypeBuilder builder() {
        return new SchemaRootTypeBuilder();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class SchemaRootTypeBuilder extends BaseCommonTypeBuilder<SchemaRootTypeBuilder> {
        private String schemaRef;
        private Map<String, JsonDefinitionsType> definitions;
        private Map<String, JsonSchemaObject> properties;
        private Boolean additionalProperties;
        private Set<String> required;

        private SchemaRootTypeBuilder() {
            reset();
        }

        protected void reset() {
            super.reset();
            this.type(null);
            this.schemaRef = null;
            this.definitions = new LinkedHashMap<>();
            this.properties = new LinkedHashMap<>();
            this.required = new TreeSet<>();
            this.additionalProperties = null;
        }

        public SchemaRootTypeBuilder schemaRef(@NotNull final String schemaRef) {
            this.schemaRef = schemaRef;
            return getThis();
        }

        public SchemaRootTypeBuilder addDefinitions(@NotNull final String key,
                                                    @NotNull final JsonDefinitionsType value) {
            this.definitions.put(key, value);
            return getThis();
        }

        public SchemaRootTypeBuilder addProperty(@NotNull final String key,
                                                 @NotNull final JsonSchemaObject value) {
            this.properties.put(key, value);
            return getThis();
        }

        public SchemaRootTypeBuilder addRequiredProperty(@NotNull final String key,
                                                         @NotNull final JsonSchemaObject value) {
            this.addProperty(key, value);
            this.required.add(key);
            return getThis();
        }

        public SchemaRootTypeBuilder disallowAdditionalProperties() {
            this.additionalProperties = false;
            return getThis();
        }

        @Override
        public SchemaRootTypeBuilder type(@Nullable final JsonSimpleType ignored) {
            return super.type(null);
        }

        public SchemaRootType build() {
            SchemaRootType value = new SchemaRootType(this);
            this.reset();
            return value;
        }

        protected SchemaRootTypeBuilder getThis() {
            return this;
        }
    }
}
