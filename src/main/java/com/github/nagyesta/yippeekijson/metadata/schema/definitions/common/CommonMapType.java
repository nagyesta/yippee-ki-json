package com.github.nagyesta.yippeekijson.metadata.schema.definitions.common;

import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSimpleType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Definition used for common map types in our JSON Schema.
 * We can only define the child property types using this representation.
 */
@Getter
public final class CommonMapType extends BaseCommonType {

    private final Map<String, JsonSimpleType> additionalProperties;
    private final Boolean uniqueItems = true;

    private CommonMapType(@NotNull final CommonMapTypeBuilder builder) {
        super(builder);
        this.additionalProperties = Map.of("type", builder.innerType);
    }

    public static CommonMapTypeBuilder builder() {
        return new CommonMapTypeBuilder();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class CommonMapTypeBuilder extends BaseCommonTypeBuilder<CommonMapTypeBuilder> {
        private JsonSimpleType innerType;

        private CommonMapTypeBuilder() {
            reset();
        }

        protected void reset() {
            super.reset();
            super.type(JsonSimpleType.OBJECT);
            this.innerType = null;
        }

        public CommonMapTypeBuilder innerType(@NotNull final JsonSimpleType innerType) {
            this.innerType = innerType;
            return getThis();
        }

        public CommonMapType build() {
            CommonMapType value = new CommonMapType(this);
            this.reset();
            return value;
        }

        protected CommonMapTypeBuilder getThis() {
            return this;
        }
    }
}
