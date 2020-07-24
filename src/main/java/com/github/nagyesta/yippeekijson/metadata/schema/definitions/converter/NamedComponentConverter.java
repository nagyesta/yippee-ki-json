package com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter;

import com.github.nagyesta.yippeekijson.metadata.schema.definitions.CompositeTypeDefinitionRegistry;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.NamedJsonSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import org.jetbrains.annotations.NotNull;

/**
 * Converts named components to JSON schema type definitions.
 */
public interface NamedComponentConverter {

    /**
     * The key of the property storing the name of the component.
     */
    String PROPERTY_NAME = "name";

    /**
     * Converts a single named component context to a JSON type definition representing it.
     *
     * @param componentContext The component context we need to convert
     * @return The JSON type definition
     */
    @NotNull
    NamedJsonSchemaTypeDefinition convert(@NotNull ComponentContext componentContext);

    /**
     * Evaluates whether this type can convert the given component context.
     *
     * @param componentContext The component context we want to convert
     * @return true if the conversion ot this type is supported, false otherwise
     */
    boolean supports(@NotNull ComponentContext componentContext);

    /**
     * Sets the {@link CompositeTypeDefinitionRegistry} to allow type resolution.
     *
     * @param typeDefinitionRegistry the registry.
     */
    void setTypeDefinitionRegistry(@NotNull CompositeTypeDefinitionRegistry typeDefinitionRegistry);
}
