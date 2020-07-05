package com.github.nagyesta.yippeekijson.metadata.schema.definitions;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Exports schema for named components.
 */
public interface JsonSchemaExporter {

    /**
     * Exports generated schema stitched together for the named components we use.
     *
     * @return the fully functional JSON schema document as a String
     * @throws JsonProcessingException When the schema object cannot be serialized.
     */
    String exportSchema() throws JsonProcessingException;
}
