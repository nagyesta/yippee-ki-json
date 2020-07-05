package com.github.nagyesta.yippeekijson.metadata.schema.markdown;

import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentType;

import java.io.IOException;
import java.util.Map;

/**
 * Exports documentation for named components.
 */
public interface DocumentationExporter {

    /**
     * Exports generated documentation stitched together on a per file basis as defined in the
     * schema definition annotations.
     *
     * @param componentType The type of the component we wan tot export
     * @return a Map of documents, keyed by the file name and value is the file content
     * @throws IOException In case templates couldn't be opened
     */
    Map<String, String> exportDocumentation(ComponentType componentType) throws IOException;
}
