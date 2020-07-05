package com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaObject;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Implementation of if-else types.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonRefSchemaTypeDefinition implements JsonSchemaObject {

    @JsonProperty("$ref")
    private final String ref;

    private JsonRefSchemaTypeDefinition(@NotNull final JsonRefSchemaTypeDefinitionBuilder builder) {
        this.ref = builder.ref;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static JsonRefSchemaTypeDefinition ref(@NotNull final Optional<String> ref) {
        return ref(ref.orElseThrow(() -> new IllegalArgumentException("Empty ref found.")));
    }

    public static JsonRefSchemaTypeDefinition ref(@NotNull final String ref) {
        return builder().ref(ref).build();
    }

    public static JsonRefSchemaTypeDefinitionBuilder builder() {
        return new JsonRefSchemaTypeDefinitionBuilder();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class JsonRefSchemaTypeDefinitionBuilder {
        private String ref;

        private JsonRefSchemaTypeDefinitionBuilder() {
            reset();
        }

        private void reset() {
            this.ref = null;
        }

        public JsonRefSchemaTypeDefinitionBuilder ref(@NotNull final String ref) {
            this.ref = ref;
            return this;
        }

        public JsonRefSchemaTypeDefinition build() {
            final JsonRefSchemaTypeDefinition value = new JsonRefSchemaTypeDefinition(this);
            reset();
            return value;
        }
    }
}
