package com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.impl;

import com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.PropertyContextPreprocessor;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonConstantSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonPropertiesSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonPropertiesSchemaTypeDefinition.JsonPropertiesSchemaTypeDefinitionBuilder;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentType;
import com.jayway.jsonpath.JsonPath;
import org.jetbrains.annotations.NotNull;

import static com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonRefSchemaTypeDefinition.ref;

public class JsonRuleSchemaTypeConverter extends AbstractNamedComponentConverter {

    private static final String PATH = "path";
    private static final String PARAMS = "params";

    public JsonRuleSchemaTypeConverter(@NotNull final PropertyContextPreprocessor propertyContextPreprocessor) {
        super(propertyContextPreprocessor);
    }

    @Override
    public boolean supports(@NotNull final ComponentContext componentContext) {
        return componentContext.getComponentType() == ComponentType.RULE;
    }

    @Override
    protected void appendProperties(@NotNull final JsonPropertiesSchemaTypeDefinitionBuilder builder,
                                    @NotNull final ComponentContext componentContext) {
        if (componentContext.getPathRestrictionValue() == null) {
            builder.addRequiredProperty(PATH, ref(getTypeDefinitionRegistry().toCommonTypeReference(JsonPath.class)));
        } else {
            builder.addRequiredProperty(PATH, JsonConstantSchemaTypeDefinition.builder()
                    .constant(componentContext.getPathRestrictionValue())
                    .description(componentContext.getPathRestrictionDocs())
                    .build());
        }
        if (!componentContext.getProperties().isEmpty()) {
            final JsonPropertiesSchemaTypeDefinitionBuilder paramBuilder = JsonPropertiesSchemaTypeDefinition.builder()
                    .description("Additional parameters of the rule.");
            getPropertyContextPreprocessor().preprocessProperties(componentContext.getProperties().values())
                    .forEach((name, value) -> addPropertiesTo(paramBuilder, name, value));
            builder.addRequiredProperty(PARAMS, paramBuilder
                    .propertyCounts(componentContext.getMinProperties(), componentContext.getMaxProperties())
                    .disallowAdditionalProperties()
                    .build());
        }
    }
}
