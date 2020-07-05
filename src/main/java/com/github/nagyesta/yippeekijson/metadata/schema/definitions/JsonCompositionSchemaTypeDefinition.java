package com.github.nagyesta.yippeekijson.metadata.schema.definitions;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.Map;

/**
 * Provides a way to represent "anyOf", "allOf", "oneOf" and "not" constructs.
 */
public interface JsonCompositionSchemaTypeDefinition extends JsonSchemaTypeDefinition {

    /**
     * Used for serialization of the compositions.
     *
     * @return the values in the arrays or single value if this is a "not" composition
     */
    @JsonAnyGetter
    Map<String, Object> getComposition();
}
