package com.github.nagyesta.yippeekijson.metadata.schema.definitions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.Nullable;

/**
 * Allows naming Json Schema type definitions.
 */
public interface NamedJsonSchemaTypeDefinition extends JsonSchemaTypeDefinition {

    /**
     * Returns the type name of this definition.
     *
     * @return the name of the type definition
     */
    @Nullable
    @JsonIgnore
    String getJsonTypeDefinitionName();

}
