package com.github.nagyesta.yippeekijson.metadata.schema.definitions.registry;

import com.github.nagyesta.yippeekijson.metadata.schema.annotation.CommonMapTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.CommonStringTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonCommonTypeDefinitionRegistry;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSimpleType;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.NamedJsonSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.CommonEnumType;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.CommonMapType;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.CommonStringType;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.CommonStringValuesType;
import com.github.nagyesta.yippeekijson.metadata.schema.parser.WikiMetadataParser;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.*;

public class JsonCommonTypeDefinitionRegistryImpl implements JsonCommonTypeDefinitionRegistry {

    private static final String COMMON_TYPE_DEFINITIONS_ROOT = "#/definitions/" + COMMON_TYPES + "/definitions/";
    private final WikiMetadataParser wikiMetadataParser;
    private final SortedMap<String, JsonSchemaTypeDefinition> definitions = new TreeMap<>();
    private final Map<Type, String> typeMappings = new TreeMap<>(Comparator.comparing(Type::getTypeName));

    public JsonCommonTypeDefinitionRegistryImpl(@NotNull final WikiMetadataParser wikiMetadataParser) {
        this.wikiMetadataParser = wikiMetadataParser;
    }

    @Override
    public @NotNull Map<String, JsonSchemaTypeDefinition> registeredDefinitions() {
        return Collections.unmodifiableSortedMap(definitions);
    }

    @Override
    public @NotNull Optional<String> toTypeReference(@NotNull final Type commonType) {
        return Optional.ofNullable(this.typeMappings.get(commonType)).map(this::appendPrefix);
    }

    @Override
    public void registerType(@NotNull final Type commonType,
                             @NotNull final NamedJsonSchemaTypeDefinition definition) {
        Assert.isTrue(this.toTypeReference(commonType).isEmpty(), "Type is already registered: " + commonType.getTypeName());
        this.definitions.put(definition.getJsonTypeDefinitionName(), definition);
        this.typeMappings.put(commonType, definition.getJsonTypeDefinitionName());
    }

    @NotNull
    private String appendPrefix(@NotNull final String definition) {
        return COMMON_TYPE_DEFINITIONS_ROOT + definition;
    }

    @Override
    public void registerType(@NotNull final Type commonType, @NotNull final CommonMapTypeDefinition definition) {
        final CommonMapType commonMapType = CommonMapType.builder()
                .jsonTypeDefinitionName(definition.typeName())
                .innerType(JsonSimpleType.forType(definition.valueType()))
                .comment(Optional.ofNullable(wikiMetadataParser.toUri(definition.wikiLink())).map(URI::toString).orElse(null))
                .description(definition.docs())
                .build();
        this.registerType(commonType, commonMapType);
    }

    @Override
    public void registerType(@NotNull final Type commonType, @NotNull final CommonStringTypeDefinition definition) {
        NamedJsonSchemaTypeDefinition commonTypeDefinition;
        if (!Enum.class.equals(definition.enumType())) {
            commonTypeDefinition = convertEnumType(commonType, definition);
        } else if (StringUtils.isNotBlank(definition.regex())) {
            commonTypeDefinition = convertRegexStringType(definition);
        } else {
            commonTypeDefinition = convertStringArrayType(commonType, definition);
        }
        this.registerType(commonType, commonTypeDefinition);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private NamedJsonSchemaTypeDefinition convertEnumType(@NotNull final Type commonType,
                                                          @NotNull final CommonStringTypeDefinition definition) {
        Assert.isTrue(definition.enumType().equals(commonType), "Enums must use the same registered type: " + commonType);
        return CommonEnumType.builder((Class<? extends Enum<?>>) definition.enumType())
                .jsonTypeDefinitionName(definition.typeName())
                .addAll()
                .description(definition.docs())
                .comment(Optional.ofNullable(wikiMetadataParser.toUri(definition.wikiLink())).map(URI::toString).orElse(null))
                .build();
    }

    @NotNull
    private NamedJsonSchemaTypeDefinition convertRegexStringType(@NotNull final CommonStringTypeDefinition definition) {
        return CommonStringType.builder()
                .jsonTypeDefinitionName(definition.typeName())
                .pattern(definition.regex())
                .comment(Optional.ofNullable(wikiMetadataParser.toUri(definition.wikiLink())).map(URI::toString).orElse(null))
                .description(definition.docs())
                .build();
    }

    @NotNull
    private NamedJsonSchemaTypeDefinition convertStringArrayType(@NotNull final Type commonType,
                                                                 @NotNull final CommonStringTypeDefinition definition) {
        Assert.notEmpty(definition.values(), "Array of allowed values cannot be empty: " + commonType);
        return CommonStringValuesType.builder()
                .jsonTypeDefinitionName(definition.typeName())
                .addEnum(definition.values())
                .comment(Optional.ofNullable(wikiMetadataParser.toUri(definition.wikiLink())).map(URI::toString).orElse(null))
                .description(definition.docs())
                .build();
    }
}
