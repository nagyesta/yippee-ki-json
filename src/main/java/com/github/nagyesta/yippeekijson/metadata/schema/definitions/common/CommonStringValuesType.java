package com.github.nagyesta.yippeekijson.metadata.schema.definitions.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSimpleType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Definition used for common String types in our JSON Schema.
 * Uses a set of allowed values for validation. Will define an enum in the end.
 */
@Getter
@JsonPropertyOrder({"$comment", "description", "type", "enum"})
public final class CommonStringValuesType extends BaseCommonType {

    @JsonProperty("enum")
    private final List<String> enumValues;
    private final boolean additionalItems;

    private CommonStringValuesType(@NotNull final CommonStringValuesTypeBuilder builder) {
        super(builder);
        this.enumValues = builder.enumValues.stream()
                .collect(Collectors.toUnmodifiableList());
        this.additionalItems = false;
    }

    public static CommonStringValuesTypeBuilder builder() {
        return new CommonStringValuesTypeBuilder();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static final class CommonStringValuesTypeBuilder extends BaseCommonTypeBuilder<CommonStringValuesTypeBuilder> {
        private Set<String> enumValues;

        private CommonStringValuesTypeBuilder() {
            reset();
        }

        protected void reset() {
            super.reset();
            super.type(JsonSimpleType.STRING);
            this.enumValues = new TreeSet<>();
        }

        public CommonStringValuesTypeBuilder addEnum(@NotNull final String value) {
            this.enumValues.add(value);
            return getThis();
        }

        public CommonStringValuesTypeBuilder addEnum(@NotNull final String... values) {
            Arrays.stream(values).forEach(this::addEnum);
            return getThis();
        }

        public CommonStringValuesType build() {
            CommonStringValuesType value = new CommonStringValuesType(this);
            this.reset();
            return value;
        }

        protected CommonStringValuesTypeBuilder getThis() {
            return this;
        }
    }
}
