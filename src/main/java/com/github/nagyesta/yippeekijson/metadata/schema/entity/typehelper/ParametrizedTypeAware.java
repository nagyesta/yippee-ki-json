package com.github.nagyesta.yippeekijson.metadata.schema.entity.typehelper;

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
    Map<Class<? extends ParametrizedTypeAware>, Type> KNOWN_TYPES = Map.of(
            StringObjectMap.class, TypeUtils.parameterize(Map.class, String.class, Object.class),
            StringStringMap.class, TypeUtils.parameterize(Map.class, String.class, String.class));

    /**
     * Contains the simplified names of the known types we want to use in our named components.
     */
    Map<Type, String> TYPE_NAME_TRANSLATION = Map.of(
            KNOWN_TYPES.get(StringObjectMap.class), StringObjectMap.class.getSimpleName(),
            KNOWN_TYPES.get(StringStringMap.class), StringStringMap.class.getSimpleName(),
            String.class, String.class.getSimpleName(),
            BigDecimal.class, BigDecimal.class.getSimpleName(),
            JsonSchema.class, JsonSchema.class.getSimpleName(),
            Object.class, Object.class.getSimpleName());


    /**
     * Contains the known types we want to use in our named components (ordered from most specific to least specific).
     */
    List<Type> TYPE_TRANSLATION_PRECEDENCE = List.of(
            KNOWN_TYPES.get(StringObjectMap.class),
            KNOWN_TYPES.get(StringStringMap.class),
            String.class,
            BigDecimal.class,
            JsonSchema.class,
            Object.class);
}
