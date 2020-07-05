package com.github.nagyesta.yippeekijson.metadata.schema.definitions.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSimpleType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Definition used for common enum types in our JSON Schema.
 */
@Getter
@JsonPropertyOrder({"$comment", "description", "type", "enum", "additionalItems"})
public final class CommonEnumType extends BaseCommonType {

    @JsonProperty("enum")
    private final List<String> enumValues;
    private final boolean additionalItems;

    private CommonEnumType(@NotNull final CommonEnumTypeBuilder builder) {
        super(builder);
        this.enumValues = builder.enumValues.stream()
                .map(Enum::name)
                .collect(Collectors.toUnmodifiableList());
        this.additionalItems = false;
    }

    public static CommonEnumTypeBuilder builder(final Class<? extends Enum<?>> enumClass) {
        return new CommonEnumTypeBuilder(enumClass);
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class CommonEnumTypeBuilder extends BaseCommonTypeBuilder<CommonEnumTypeBuilder> {
        private final Class<? extends Enum<?>> enumClass;
        private Set<Enum<?>> enumValues;

        private CommonEnumTypeBuilder(@NotNull final Class<? extends Enum<?>> enumClass) {
            this.enumClass = enumClass;
            reset();
        }

        protected void reset() {
            super.reset();
            super.type(JsonSimpleType.STRING);
            this.enumValues = new TreeSet<>();
        }

        public CommonEnumTypeBuilder addEnum(@NotNull final Enum<?> value) {
            Assert.isInstanceOf(enumClass, value);
            this.enumValues.add(value);
            return getThis();
        }

        public CommonEnumTypeBuilder addEnum(@NotNull final Enum<?>... values) {
            Arrays.stream(values).forEach(this::addEnum);
            return getThis();
        }

        public CommonEnumTypeBuilder addAll() {
            return this.addEnum(enumClass.getEnumConstants());
        }

        public CommonEnumType build() {
            CommonEnumType value = new CommonEnumType(this);
            this.reset();
            return value;
        }

        protected CommonEnumTypeBuilder getThis() {
            return this;
        }
    }
}
