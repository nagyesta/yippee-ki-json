package com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.impl;

import com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.PropertyContextPreprocessor;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.PropertyContextWrapper;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.PropertyContext;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.PropertyContextWrapper.PropertyContextWrapperBuilder;
import static com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.PropertyContextWrapper.builder;

public class PropertyContextPreprocessorImpl implements PropertyContextPreprocessor {

    @Override
    public Map<String, PropertyContextWrapper> preprocessProperties(final Collection<PropertyContext> properties) {
        Map<String, PropertyContextWrapperBuilder> combinedProperties = new LinkedHashMap<>();
        properties.forEach((value) -> combinedProperties
                .computeIfAbsent(value.getName(), name -> builder())
                .merge(value));
        final Map<String, PropertyContextWrapper> result = new LinkedHashMap<>();
        combinedProperties.forEach((k, v) -> result.put(k, v.build()));
        return result;
    }
}
