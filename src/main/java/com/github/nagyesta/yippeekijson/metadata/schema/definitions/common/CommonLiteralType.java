package com.github.nagyesta.yippeekijson.metadata.schema.definitions.common;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Definition used for common literal types in our JSON Schema.
 */
@Getter
public final class CommonLiteralType extends BaseCommonType {

    private CommonLiteralType(@NotNull final CommonLiteralTypeBuilder builder) {
        super(builder);
    }

    public static CommonLiteralType description(@NotNull final String description) {
        return CommonLiteralType.builder().description(description).build();
    }

    public static CommonLiteralTypeBuilder builder() {
        return new CommonLiteralTypeBuilder();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class CommonLiteralTypeBuilder extends BaseCommonTypeBuilder<CommonLiteralTypeBuilder> {

        private CommonLiteralTypeBuilder() {
            reset();
        }

        protected void reset() {
            super.reset();
            super.type(null);
        }

        public CommonLiteralType build() {
            CommonLiteralType value = new CommonLiteralType(this);
            this.reset();
            return value;
        }

        protected CommonLiteralTypeBuilder getThis() {
            return this;
        }
    }
}
