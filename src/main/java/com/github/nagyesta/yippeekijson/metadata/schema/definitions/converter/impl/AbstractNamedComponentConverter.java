package com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.impl;

import com.github.nagyesta.yippeekijson.core.NamedComponentUtil;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.CompositeTypeDefinitionRegistry;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaObject;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSimpleType;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.NamedJsonSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.CommonEnumType;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.CommonLiteralType;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.CommonStringType;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.NamedComponentConverter;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.PropertyContextPreprocessor;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.PropertyContextWrapper;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonArraySchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonArraySchemaTypeDefinition.JsonArraySchemaTypeDefinitionBuilder;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonCompositionSchemaTypeDefinitionImpl;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonConstantSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonPropertiesSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonPropertiesSchemaTypeDefinition.JsonPropertiesSchemaTypeDefinitionBuilder;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentType;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.DocumentationContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.PropertyContext;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.Assert;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.CommonLiteralType.description;
import static com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonRefSchemaTypeDefinition.ref;

@Getter
public abstract class AbstractNamedComponentConverter implements NamedComponentConverter {
    private final PropertyContextPreprocessor propertyContextPreprocessor;
    private CompositeTypeDefinitionRegistry typeDefinitionRegistry;

    public AbstractNamedComponentConverter(@NotNull final PropertyContextPreprocessor propertyContextPreprocessor) {
        this.propertyContextPreprocessor = propertyContextPreprocessor;
    }

    @Override
    @NotNull
    public NamedJsonSchemaTypeDefinition convert(@NotNull final ComponentContext componentContext) {
        Assert.isTrue(this.supports(componentContext), "This type is not supported: " + componentContext.getComponentType());
        final JsonPropertiesSchemaTypeDefinitionBuilder builder = JsonPropertiesSchemaTypeDefinition.builder()
                .jsonTypeDefinitionName(componentContext.getJsonTypeName())
                .comment(optionalWikiLink(componentContext))
                .description(componentContext.getDocumentation().getSectionTitle())
                .disallowAdditionalProperties()
                .addRequiredProperty(PROPERTY_NAME, JsonConstantSchemaTypeDefinition.builder()
                        .constant(componentContext.getComponentName())
                        .description("The name of the component.")
                        .build());

        appendProperties(builder, componentContext);
        return builder.build();
    }

    /**
     * Provides a way to append the properties of the named component to the schema node we are building.
     *
     * @param builder          The builder where we want to add the properties
     * @param componentContext The input parsed context
     */
    protected abstract void appendProperties(@NotNull JsonPropertiesSchemaTypeDefinitionBuilder builder,
                                             @NotNull ComponentContext componentContext);

    @Override
    public boolean supports(@NotNull final ComponentContext componentContext) {
        return componentContext.getComponentType() != ComponentType.RULE;
    }

    /**
     * Adds a property to the hierarchy we are building.
     *
     * @param builder the destination builder
     * @param name    the name of the property
     * @param value   the value of the property
     */
    protected void addPropertiesTo(@NotNull final JsonPropertiesSchemaTypeDefinitionBuilder builder,
                                   @NotNull final String name,
                                   @NotNull final PropertyContextWrapper value) {
        if (value.getWrapped().isPresent()) {
            PropertyContext propertyValue = value.getWrapped().get();
            JsonSchemaObject definition = convertParameter(propertyValue, value.isCollection(), false);
            builder.addProperty(propertyValue.isRequired(), name, definition);
        } else {
            Assert.isTrue(value.getChildren().isPresent(), "Map must be populated.");
            builder.addRequiredProperty(name, processNestedProperties(value.getChildren().get()));
        }
    }

    @NotNull
    private JsonSchemaObject processNestedProperties(final Map<String, PropertyContextWrapper> map) {
        JsonPropertiesSchemaTypeDefinitionBuilder nested = JsonPropertiesSchemaTypeDefinition.builder()
                .disallowAdditionalProperties();
        map.forEach((nestedName, nestedValue) -> addPropertiesTo(nested, nestedName, nestedValue));
        return nested.build();
    }

    @NotNull
    private JsonSchemaObject convertParameter(@NotNull final PropertyContext value,
                                              final boolean collection,
                                              final boolean suppressDocs) {
        JsonSchemaObject definition;
        final String ref = extractRef(value);
        if (StringUtils.isNotBlank(ref)) {
            definition = convertRefParameter(value, ref, collection, suppressDocs);
        } else {
            Assert.isTrue(value.getChild().isEmpty(), "Only leaf nodes are supported, but parent found: " + value.getName());
            definition = convertSimpleParameter(value, collection, suppressDocs);
        }
        return definition;
    }

    @NotNull
    private JsonSchemaObject convertRefParameter(@NotNull final PropertyContext value,
                                                 @NotNull final String ref,
                                                 final boolean collection,
                                                 final boolean suppressDocs) {
        String description = convertDocs(value, suppressDocs);
        if (collection) {
            return buildSimpleCollectionParameter(value, true);
        } else if (description == null) {
            return ref(ref);
        } else {
            return buildRefWithDescriptionParameter(ref, description);
        }
    }

    @NotNull
    private JsonSchemaObject buildRefWithDescriptionParameter(@NotNull final String ref,
                                                              @NotNull final String description) {
        return JsonCompositionSchemaTypeDefinitionImpl.builder()
                .allOf(List.of(ref(ref), description(description)))
                .build();
    }

    @Nullable
    private String extractRef(@NotNull final PropertyContext value) {
        final Optional<String> commonTypeRef = value.getCommonTypeRef();
        final Optional<String> reference;
        if (TypeUtils.isAssignable(value.getType(), Supplier.class)) {
            final Type outputType = NamedComponentUtil.supplierTypeParameterOf(value.getType());
            reference = typeDefinitionRegistry.toSupplierTypeReference(outputType);
        } else if (TypeUtils.isAssignable(value.getType(), Predicate.class)) {
            final Type inputType = NamedComponentUtil.predicateTypeParameterOf(value.getType());
            reference = typeDefinitionRegistry.toPredicateTypeReference(inputType);
        } else if (TypeUtils.isAssignable(value.getType(), Function.class)) {
            final Type inputType = NamedComponentUtil.functionInputTypeParameterOf(value.getType());
            final Type outputType = NamedComponentUtil.functionOutputTypeParameterOf(value.getType());
            reference = typeDefinitionRegistry.toFunctionTypeReference(inputType, outputType);
        } else {
            reference = typeDefinitionRegistry.toCommonTypeReference(value.getType());
        }
        return commonTypeRef.orElse(reference.orElse(null));
    }

    @NotNull
    private JsonSchemaObject convertSimpleParameter(@NotNull final PropertyContext value,
                                                    final boolean collection,
                                                    final boolean suppressDocs) {
        if (collection) {
            return buildSimpleCollectionParameter(value, suppressDocs);
        } else if (isEnumTyped(value)) {
            return buildEnumParameter(value, suppressDocs);
        } else if (value.getType().equals(String.class) && value.getPattern() != null) {
            return buildPatternValidatedStringParameter(value, suppressDocs);
        } else {
            return buildSimpleLiteralParameter(value, suppressDocs);
        }
    }

    private boolean isEnumTyped(@NotNull final PropertyContext value) {
        return value.getType() instanceof Class && ((Class<?>) value.getType()).isEnum();
    }

    @NotNull
    private JsonSchemaObject buildSimpleLiteralParameter(@NotNull final PropertyContext value, final boolean suppressDocs) {
        return CommonLiteralType.builder()
                .type(JsonSimpleType.forType(value.getType()))
                .description(convertDocs(value, suppressDocs))
                .build();
    }

    @NotNull
    private JsonSchemaObject buildPatternValidatedStringParameter(@NotNull final PropertyContext value, final boolean suppressDocs) {
        return CommonStringType.builder()
                .type(JsonSimpleType.STRING)
                .description(convertDocs(value, suppressDocs))
                .pattern(value.getPattern())
                .build();
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private JsonSchemaObject buildEnumParameter(@NotNull final PropertyContext value, final boolean suppressDocs) {
        return CommonEnumType.builder((Class<? extends Enum<?>>) value.getType())
                .addAll()
                .description(convertDocs(value, suppressDocs))
                .build();
    }

    @NotNull
    private JsonSchemaObject buildSimpleCollectionParameter(@NotNull final PropertyContext value, final boolean suppressDocs) {
        final JsonArraySchemaTypeDefinitionBuilder builder = JsonArraySchemaTypeDefinition.builder()
                .items(convertParameter(value, false, true))
                .description(convertDocs(value, suppressDocs))
                .disallowAdditionalItems();
        if (value.isRequired()) {
            builder.itemCounts(1, null);
        }
        return builder.build();
    }

    @Nullable
    private String convertDocs(@NotNull final PropertyContext value,
                               final boolean suppressDocs) {
        if (suppressDocs) {
            return null;
        }
        return StringUtils.trimToNull(value.getDocs().orElse(null));
    }

    private String optionalWikiLink(@NotNull final ComponentContext componentContext) {
        return Optional.ofNullable(componentContext.getDocumentation())
                .map(DocumentationContext::getWikiReference)
                .map(URI::toString)
                .orElse(null);
    }

    @Override
    public void setTypeDefinitionRegistry(@NotNull final CompositeTypeDefinitionRegistry typeDefinitionRegistry) {
        this.typeDefinitionRegistry = typeDefinitionRegistry;
    }
}
