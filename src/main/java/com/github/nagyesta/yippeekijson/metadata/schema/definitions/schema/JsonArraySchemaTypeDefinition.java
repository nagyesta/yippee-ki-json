package com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaObject;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSimpleType;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.BaseCommonType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Definition used for array types with pre-defined item types in our JSON Schema.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"$comment", "description", "type", "items", "minItems", "maxItems", "additionalItems"})
public final class JsonArraySchemaTypeDefinition extends BaseCommonType {

    private final JsonSchemaObject items;
    private final Integer minItems;
    private final Integer maxItems;
    private final Boolean additionalItems;

    private JsonArraySchemaTypeDefinition(@NotNull final JsonArraySchemaTypeDefinitionBuilder builder) {
        super(builder);
        this.items = builder.items;
        this.minItems = builder.minItems;
        this.maxItems = builder.maxItems;
        this.additionalItems = builder.additionalItems;
    }

    public static JsonArraySchemaTypeDefinitionBuilder builder() {
        return new JsonArraySchemaTypeDefinitionBuilder();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class JsonArraySchemaTypeDefinitionBuilder
            extends BaseCommonTypeBuilder<JsonArraySchemaTypeDefinitionBuilder> {
        private JsonSchemaObject items;
        private Integer minItems;
        private Integer maxItems;
        private Boolean additionalItems;

        private JsonArraySchemaTypeDefinitionBuilder() {
            reset();
        }

        protected void reset() {
            super.reset();
            this.type(JsonSimpleType.ARRAY);
            this.items = null;
            this.minItems = null;
            this.maxItems = null;
        }

        public JsonArraySchemaTypeDefinitionBuilder items(@NotNull final JsonSchemaObject items) {
            this.items = items;
            return getThis();
        }

        public JsonArraySchemaTypeDefinitionBuilder itemCounts(@Nullable final Integer minItems,
                                                               @Nullable final Integer maxItems) {
            this.minItems = minItems;
            this.maxItems = maxItems;
            return getThis();
        }

        public JsonArraySchemaTypeDefinitionBuilder allowAdditionalItems() {
            this.additionalItems = true;
            return this;
        }

        public JsonArraySchemaTypeDefinitionBuilder disallowAdditionalItems() {
            this.additionalItems = false;
            return this;
        }

        @Override
        public JsonArraySchemaTypeDefinitionBuilder type(@Nullable final JsonSimpleType ignored) {
            super.type(JsonSimpleType.ARRAY);
            return getThis();
        }

        public JsonArraySchemaTypeDefinition build() {
            JsonArraySchemaTypeDefinition value = new JsonArraySchemaTypeDefinition(this);
            this.reset();
            return value;
        }

        protected JsonArraySchemaTypeDefinitionBuilder getThis() {
            return this;
        }
    }
}
