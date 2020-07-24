package com.github.nagyesta.yippeekijson.metadata.schema.definitions.common;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSimpleType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Definition used for common String types in our JSON Schema.
 * Uses a RegEx pattern for validation.
 */
@Getter
@JsonPropertyOrder({"$comment", "description", "type", "pattern"})
public final class CommonStringType extends BaseCommonType {

    private final String pattern;

    private CommonStringType(@NotNull final CommonStringTypeBuilder builder) {
        super(builder);
        this.pattern = builder.pattern;
    }

    public static CommonStringTypeBuilder builder() {
        return new CommonStringTypeBuilder();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class CommonStringTypeBuilder extends BaseCommonTypeBuilder<CommonStringTypeBuilder> {
        private String pattern;

        private CommonStringTypeBuilder() {
            reset();
        }

        protected void reset() {
            super.reset();
            super.type(JsonSimpleType.STRING);
            this.pattern = null;
        }

        public CommonStringTypeBuilder pattern(@NotNull final String pattern) {
            this.pattern = pattern;
            return getThis();
        }

        public CommonStringType build() {
            CommonStringType value = new CommonStringType(this);
            this.reset();
            return value;
        }

        protected CommonStringTypeBuilder getThis() {
            return this;
        }
    }
}
