package com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaObject;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSimpleType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Definition used for constant valued String types in our JSON Schema.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"type", "const", "description"})
public final class JsonConstantSchemaTypeDefinition implements JsonSchemaObject {

    @JsonProperty("const")
    private final String constant;
    private final JsonSimpleType type;
    private final String description;

    private JsonConstantSchemaTypeDefinition(@NotNull final JsonConstantSchemaTypeDefinitionBuilder builder) {
        this.type = JsonSimpleType.STRING;
        this.constant = builder.constant;
        this.description = builder.description;
    }

    public static JsonConstantSchemaTypeDefinitionBuilder builder() {
        return new JsonConstantSchemaTypeDefinitionBuilder();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class JsonConstantSchemaTypeDefinitionBuilder {
        private String constant;
        private String description;

        private JsonConstantSchemaTypeDefinitionBuilder() {
            reset();
        }

        private void reset() {
            this.constant = null;
            this.description = null;
        }

        public JsonConstantSchemaTypeDefinitionBuilder constant(@NotNull final String constant) {
            this.constant = constant;
            return this;
        }

        public JsonConstantSchemaTypeDefinitionBuilder description(@Nullable final String description) {
            this.description = description;
            return this;
        }

        public JsonConstantSchemaTypeDefinition build() {
            JsonConstantSchemaTypeDefinition value = new JsonConstantSchemaTypeDefinition(this);
            this.reset();
            return value;
        }
    }
}
