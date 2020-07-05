package com.github.nagyesta.yippeekijson.metadata.schema.definitions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.nagyesta.yippeekijson.core.NamedComponentUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JSON Schema simple types.
 */
public enum JsonSimpleType {
    /**
     * Array.
     */
    @JsonProperty("array")
    ARRAY(Collection.class),
    /**
     * Boolean.
     */
    @JsonProperty("boolean")
    BOOLEAN(Boolean.class),
    /**
     * Integer.
     */
    @JsonProperty("integer")
    INTEGER(Integer.class, BigInteger.class),
    /**
     * Null.
     */
    @JsonProperty("null")
    NULL(Void.class),
    /**
     * Number.
     */
    @JsonProperty("number")
    NUMBER(Double.class, BigDecimal.class),
    /**
     * Onject.
     */
    @JsonProperty("object")
    OBJECT(Map.class),
    /**
     * String.
     */
    @JsonProperty("string")
    STRING(String.class);

    private final Set<Class<?>> classes;

    JsonSimpleType(final Class<?>... classes) {
        this.classes = Arrays.stream(classes).collect(Collectors.toSet());
    }

    /**
     * Resolves an enum value for a provided type.
     *
     * @param target the type we want to find an enum for.
     * @return the resolved enum.
     */
    public static JsonSimpleType forType(final Type target) {
        Class<?> targetClass = NamedComponentUtil.asRawClass(target);
        return Arrays.stream(JsonSimpleType.values())
                .filter(e -> e.classes.stream().anyMatch(type -> type.isAssignableFrom(targetClass)))
                .findFirst()
                .orElse(OBJECT);
    }
}
