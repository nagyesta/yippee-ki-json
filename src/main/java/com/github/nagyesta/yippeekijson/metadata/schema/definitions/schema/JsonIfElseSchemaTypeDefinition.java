package com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaObject;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of if-else types.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"if", "then", "else"})
public final class JsonIfElseSchemaTypeDefinition implements JsonSchemaObject {

    @JsonProperty("if")
    private final JsonSchemaObject ifNode;
    @JsonProperty("then")
    private final JsonSchemaObject thenNode;
    @JsonProperty("else")
    private final JsonSchemaObject elseNode;

    private JsonIfElseSchemaTypeDefinition(@NotNull final JsonIfElseSchemaTypeDefinitionBuilder builder) {
        this.ifNode = builder.ifNode;
        this.thenNode = builder.thenNode;
        this.elseNode = builder.elseNode;
    }

    public static JsonIfElseSchemaTypeDefinitionBuilder builder() {
        return new JsonIfElseSchemaTypeDefinitionBuilder();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class JsonIfElseSchemaTypeDefinitionBuilder {
        private JsonSchemaObject ifNode;
        private JsonSchemaObject thenNode;
        private JsonSchemaObject elseNode;

        private JsonIfElseSchemaTypeDefinitionBuilder() {
            reset();
        }

        private void reset() {
            this.ifNode = null;
            this.thenNode = null;
            this.elseNode = null;
        }

        public JsonIfElseSchemaTypeDefinitionBuilder ifNode(@NotNull final JsonSchemaObject ifNode) {
            this.ifNode = ifNode;
            return this;

        }

        public JsonIfElseSchemaTypeDefinitionBuilder thenNode(@NotNull final JsonSchemaObject thenNode) {
            this.thenNode = thenNode;
            return this;
        }

        public JsonIfElseSchemaTypeDefinitionBuilder elseNode(@NotNull final JsonSchemaObject elseNode) {
            this.elseNode = elseNode;
            return this;
        }

        public JsonIfElseSchemaTypeDefinition build() {
            final JsonIfElseSchemaTypeDefinition value = new JsonIfElseSchemaTypeDefinition(this);
            reset();
            return value;
        }
    }
}
