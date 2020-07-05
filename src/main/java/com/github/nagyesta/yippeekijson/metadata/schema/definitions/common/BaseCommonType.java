package com.github.nagyesta.yippeekijson.metadata.schema.definitions.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSimpleType;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.NamedJsonSchemaTypeDefinition;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseCommonType implements NamedJsonSchemaTypeDefinition {

    @JsonIgnore
    private final String jsonTypeDefinitionName;
    private final JsonSimpleType type;
    private final String description;
    @JsonProperty("$comment")
    private final String comment;

    protected BaseCommonType(@NotNull final BaseCommonTypeBuilder<?> builder) {
        this.jsonTypeDefinitionName = builder.jsonTypeDefinitionName;
        this.type = builder.type;
        this.description = builder.description;
        this.comment = builder.comment;
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public abstract static class BaseCommonTypeBuilder<T extends BaseCommonTypeBuilder<?>> {
        private String jsonTypeDefinitionName;
        private JsonSimpleType type;
        private String description;
        private String comment;

        protected BaseCommonTypeBuilder() {
            reset();
        }

        protected void reset() {
            this.jsonTypeDefinitionName = null;
            this.type = JsonSimpleType.STRING;
            this.description = null;
            this.comment = null;
        }

        public T jsonTypeDefinitionName(@Nullable final String jsonTypeDefinitionName) {
            this.jsonTypeDefinitionName = jsonTypeDefinitionName;
            return getThis();
        }

        public T type(@Nullable final JsonSimpleType type) {
            this.type = type;
            return getThis();
        }

        public T description(@Nullable final String description) {
            this.description = description;
            return getThis();
        }

        public T comment(@Nullable final String comment) {
            this.comment = comment;
            return getThis();
        }

        protected abstract T getThis();

    }
}
