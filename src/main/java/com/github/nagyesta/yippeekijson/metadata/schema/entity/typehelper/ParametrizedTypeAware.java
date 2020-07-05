package com.github.nagyesta.yippeekijson.metadata.schema.entity.typehelper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.networknt.schema.JsonSchema;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Allows us to define parametrized types deep in the annotation hierarchy when the actual type
 * parameters couldn't be added due to the limitations of our annotations.
 */
@SuppressWarnings("checkstyle:InterfaceIsType")
public interface ParametrizedTypeAware {

    /**
     * Contains all of the exceptional classes using {@link ParametrizedTypeAware} as a workaround
     * to provide more information about the actual type of a parameter.
     */
    Map<Class<? extends ParametrizedTypeAware>, Type> KNOWN_TYPES = ImmutableMap
            .<Class<? extends ParametrizedTypeAware>, Type>builder()
            .put(StringObjectMap.class, TypeUtils.parameterize(Map.class, String.class, Object.class))
            .put(StringStringMap.class, TypeUtils.parameterize(Map.class, String.class, String.class))
            .build();

    /**
     * Contains the simplified names of the known types we want to use in our named components.
     */
    Map<Type, String> TYPE_NAME_TRANSLATION = ImmutableMap
            .<Type, String>builder()
            .put(KNOWN_TYPES.get(StringObjectMap.class), StringObjectMap.class.getSimpleName())
            .put(KNOWN_TYPES.get(StringStringMap.class), StringStringMap.class.getSimpleName())
            .put(String.class, String.class.getSimpleName())
            .put(BigDecimal.class, BigDecimal.class.getSimpleName())
            .put(JsonSchema.class, JsonSchema.class.getSimpleName())
            .put(Object.class, Object.class.getSimpleName())
            .build();


    /**
     * Contains the known types we want to use in our named components (ordered from most specific to least specific).
     */
    List<Type> TYPE_TRANSLATION_PRECEDENCE = ImmutableList
            .<Type>builder()
            .add(KNOWN_TYPES.get(StringObjectMap.class))
            .add(KNOWN_TYPES.get(StringStringMap.class))
            .add(String.class)
            .add(BigDecimal.class)
            .add(JsonSchema.class)
            .add(Object.class)
            .build();
}
