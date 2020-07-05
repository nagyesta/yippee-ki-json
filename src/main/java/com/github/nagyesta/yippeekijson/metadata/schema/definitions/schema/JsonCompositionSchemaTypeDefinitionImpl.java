package com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonCompositionSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaObject;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.BaseCommonType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Implementation of "anyOf", "allOf", "oneOf" and "not" compositions.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class JsonCompositionSchemaTypeDefinitionImpl extends BaseCommonType implements JsonCompositionSchemaTypeDefinition {

    @JsonIgnore
    private final List<JsonSchemaObject> items;
    @JsonIgnore
    private final CompositionType compositionType;

    private JsonCompositionSchemaTypeDefinitionImpl(@NotNull final JsonCompositionSchemaTypeDefinitionImplBuilder builder) {
        super(builder);
        this.compositionType = builder.compositionType;
        this.items = Collections.unmodifiableList(List.copyOf(builder.items));
    }

    public static JsonCompositionSchemaTypeDefinitionImplBuilder builder() {
        return new JsonCompositionSchemaTypeDefinitionImplBuilder();
    }

    @Override
    @JsonAnyGetter
    public Map<String, Object> getComposition() {
        if (compositionType == CompositionType.NOT) {
            return Collections.singletonMap(compositionType.getField(), items.get(0));
        }
        return Collections.singletonMap(compositionType.getField(), items);
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class JsonCompositionSchemaTypeDefinitionImplBuilder
            extends BaseCommonTypeBuilder<JsonCompositionSchemaTypeDefinitionImplBuilder> {
        private List<JsonSchemaObject> items;
        private CompositionType compositionType;

        private JsonCompositionSchemaTypeDefinitionImplBuilder() {
            reset();
        }

        @Override
        protected void reset() {
            super.reset();
            super.type(null);
            this.items = null;
            this.compositionType = null;
        }

        public JsonCompositionSchemaTypeDefinitionImplBuilder anyOf(@NotNull final List<JsonSchemaObject> items) {
            this.items = List.copyOf(items);
            this.compositionType = CompositionType.ANY_OF;
            return this;
        }

        public JsonCompositionSchemaTypeDefinitionImplBuilder allOf(@NotNull final List<JsonSchemaObject> items) {
            this.items = List.copyOf(items);
            this.compositionType = CompositionType.ALL_OF;
            return this;
        }

        public JsonCompositionSchemaTypeDefinitionImplBuilder oneOf(@NotNull final List<JsonSchemaObject> items) {
            this.items = List.copyOf(items);
            this.compositionType = CompositionType.ONE_OF;
            return this;
        }

        public JsonCompositionSchemaTypeDefinitionImplBuilder not(@NotNull final JsonSchemaObject item) {
            this.items = Collections.singletonList(item);
            this.compositionType = CompositionType.NOT;
            return this;
        }

        public JsonCompositionSchemaTypeDefinitionImpl build() {
            final JsonCompositionSchemaTypeDefinitionImpl value = new JsonCompositionSchemaTypeDefinitionImpl(this);
            reset();
            return value;
        }

        @Override
        protected JsonCompositionSchemaTypeDefinitionImplBuilder getThis() {
            return this;
        }
    }
}
