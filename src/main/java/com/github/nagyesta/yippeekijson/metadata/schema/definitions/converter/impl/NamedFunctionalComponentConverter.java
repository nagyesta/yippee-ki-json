package com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.impl;

import com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.PropertyContextPreprocessor;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonPropertiesSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NamedFunctionalComponentConverter extends AbstractNamedComponentConverter {

    public NamedFunctionalComponentConverter(@NotNull final PropertyContextPreprocessor propertyContextPreprocessor) {
        super(propertyContextPreprocessor);
    }

    @Override
    protected void appendProperties(@NotNull final JsonPropertiesSchemaTypeDefinition.JsonPropertiesSchemaTypeDefinitionBuilder builder,
                                    @NotNull final ComponentContext componentContext) {

        builder.propertyCounts(adjustMandatoryPropertyCounts(componentContext.getMinProperties()),
                adjustMandatoryPropertyCounts(componentContext.getMaxProperties()));
        getPropertyContextPreprocessor().preprocessProperties(componentContext.getProperties().values())
                .forEach((name, value) -> addPropertiesTo(builder, name, value));
    }

    @Nullable
    private Integer adjustMandatoryPropertyCounts(@Nullable final Integer value) {
        if (value == null) {
            return value;
        }
        return value + 1;
    }
}
