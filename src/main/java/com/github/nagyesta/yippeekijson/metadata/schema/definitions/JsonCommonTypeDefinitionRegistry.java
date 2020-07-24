package com.github.nagyesta.yippeekijson.metadata.schema.definitions;

import com.github.nagyesta.yippeekijson.metadata.schema.annotation.CommonMapTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.CommonStringTypeDefinition;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

/**
 * Registry containing common type definitions.
 */
public interface JsonCommonTypeDefinitionRegistry {
    /**
     * Type definition group name for common types.
     */
    String COMMON_TYPES = "commonTypes";

    /**
     * Returns all known common type definitions.
     * The keys are the property names (last part of the type reference), while
     * the values are the parsed common types.
     *
     * @return map of definitions
     */
    @NotNull
    Map<String, JsonSchemaTypeDefinition> registeredDefinitions();

    /**
     * Resolves a known common type to the registered type reference String.
     *
     * @param commonType the registered type.
     * @return The resolved String (empty if the type is not known)
     */
    @NotNull
    Optional<String> toTypeReference(@NotNull Type commonType);

    /**
     * Registers a common type using the provided type.
     *
     * @param commonType The type we will use to resolve the reference later.
     * @param definition The definition of the common type.
     */
    void registerType(@NotNull Type commonType, @NotNull NamedJsonSchemaTypeDefinition definition);

    /**
     * Registers a common type using the provided type.
     *
     * @param commonType The type we will use to resolve the reference later.
     * @param definition The definition of the common type.
     */
    void registerType(@NotNull Type commonType, @NotNull CommonMapTypeDefinition definition);

    /**
     * Registers a common type using the provided type.
     *
     * @param commonType The type we will use to resolve the reference later.
     * @param definition The definition of the common type.
     */
    void registerType(@NotNull Type commonType, @NotNull CommonStringTypeDefinition definition);

}
