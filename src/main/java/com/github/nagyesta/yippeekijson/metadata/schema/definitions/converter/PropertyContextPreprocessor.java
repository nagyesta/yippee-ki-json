package com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter;

import com.github.nagyesta.yippeekijson.metadata.schema.entity.PropertyContext;

import java.util.Collection;
import java.util.Map;

/**
 * Provides preprocessing steps for {@link PropertyContext} to
 * {@link com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaObject} conversion.
 */
public interface PropertyContextPreprocessor {

    /**
     * Converts the path based representation of {@link PropertyContext} to a more practical tree like
     * construct.
     *
     * @param properties the properties we need to process
     * @return the "tree"
     */
    Map<String, PropertyContextWrapper> preprocessProperties(Collection<PropertyContext> properties);
}
