package com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSimpleType;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.BaseCommonType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Definition used for object types with pre-defined properties in our JSON Schema.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"$comment", "description", "type", "definitions"})
public final class JsonDefinitionsType extends BaseCommonType {

    private final Map<String, JsonSchemaTypeDefinition> definitions;

    private JsonDefinitionsType(@NotNull final JsonDefinitionsTypeBuilder builder) {
        super(builder);
        this.definitions = builder.definitions;
    }

    public static JsonDefinitionsTypeBuilder builder() {
        return new JsonDefinitionsTypeBuilder();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class JsonDefinitionsTypeBuilder extends BaseCommonTypeBuilder<JsonDefinitionsTypeBuilder> {
        private Map<String, JsonSchemaTypeDefinition> definitions;

        private JsonDefinitionsTypeBuilder() {
            reset();
        }

        @Override
        protected void reset() {
            super.reset();
            this.type(null);
            this.definitions = new LinkedHashMap<>();
        }

        public JsonDefinitionsTypeBuilder addAll(@NotNull final Map<String, JsonSchemaTypeDefinition> definitions) {
            this.definitions.putAll(definitions);
            return this;
        }

        public JsonDefinitionsTypeBuilder add(@NotNull final String name,
                                              @NotNull final JsonSchemaTypeDefinition definition) {
            this.definitions.put(name, definition);
            return this;
        }

        @Override
        public JsonDefinitionsTypeBuilder type(@Nullable final JsonSimpleType ignored) {
            return super.type(null);
        }

        public JsonDefinitionsType build() {
            final JsonDefinitionsType value = new JsonDefinitionsType(this);
            reset();
            return value;
        }

        @Override
        protected JsonDefinitionsTypeBuilder getThis() {
            return this;
        }
    }
}
