package com.github.nagyesta.yippeekijson.metadata.schema.definitions.registry;

import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.rule.JsonRule;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonCommonTypeDefinitionRegistry;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonNamedComponentTypeDefinitionRegistry;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSimpleType;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.CommonLiteralType;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.CommonStringValuesType;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.NamedComponentConverter;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonCompositionSchemaTypeDefinitionImpl;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonPropertiesSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.parser.ComponentContextMetadataParser;
import com.jayway.jsonpath.JsonPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.NamedComponentConverter.PROPERTY_NAME;
import static com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonRefSchemaTypeDefinition.ref;

public class JsonRuleComponentTypeDefinitionRegistry extends AbstractComponentTypeDefinitionRegistry
        implements JsonNamedComponentTypeDefinitionRegistry<JsonRule> {

    private static final String TYPE_DEFINITIONS_ROOT = "#/definitions/" + RULE_TYPES + "/definitions/";
    private static final String ANY_TYPE = "anyRuleType";
    private static final String CACHE_KEY = "'ruleDefinitions'";
    private static final String PROPERTY_PATH = "path";
    private static final String PROPERTY_PARAMS = "params";
    private final JsonCommonTypeDefinitionRegistry commonTypeDefinitionRegistry;

    public JsonRuleComponentTypeDefinitionRegistry(@NotNull final ComponentContextMetadataParser componentContextMetadataParser,
                                                   @NotNull final NamedComponentConverter namedComponentConverter,
                                                   @NotNull final JsonCommonTypeDefinitionRegistry commonTypeDefinitionRegistry) {
        super(componentContextMetadataParser, namedComponentConverter);
        this.commonTypeDefinitionRegistry = commonTypeDefinitionRegistry;
    }

    @Override
    @Cacheable(cacheNames = JSON_TYPES, key = CACHE_KEY)
    public @NotNull Map<String, JsonSchemaTypeDefinition> registeredDefinitions() {
        final LinkedHashMap<String, JsonSchemaTypeDefinition> cachedMap = new LinkedHashMap<>(definitions());
        cachedMap.put(ANY_TYPE, anyTypeDefinition());
        return cachedMap;
    }

    @NotNull
    private JsonCompositionSchemaTypeDefinitionImpl anyTypeDefinition() {
        return combinedComponentDefinitions(definitions().keySet());
    }

    @Override
    @NotNull
    protected JsonPropertiesSchemaTypeDefinition nameRestrictionEnum(@NotNull final Set<String> names) {
        return JsonPropertiesSchemaTypeDefinition.builder()
                .addRequiredProperty(PROPERTY_NAME, CommonStringValuesType.builder()
                        .addEnum(names.toArray(new String[0]))
                        .description("The name of the rule.")
                        .build())
                .addRequiredProperty(PROPERTY_PATH, ref(commonTypeDefinitionRegistry.toTypeReference(JsonPath.class)))
                .addProperty(PROPERTY_PARAMS, CommonLiteralType.builder()
                        .type(JsonSimpleType.OBJECT)
                        .description("The optional parameter map to allow configuration of the selected rule.")
                        .build())
                .disallowAdditionalProperties()
                .build();
    }

    @Override
    @NotNull
    protected String appendPrefix(@NotNull final String name) {
        return TYPE_DEFINITIONS_ROOT + name;
    }

    @Override
    public @NotNull Optional<String> toTypeReference(@Nullable final Type inputType, @Nullable final Type outputType) {
        return Optional.of(ANY_TYPE).map(this::appendPrefix);
    }

    @Override
    @CacheEvict(cacheNames = JSON_TYPES, key = CACHE_KEY)
    public void registerType(@NotNull final Class<? extends JsonRule> namedType) {
        parseAndRegister(namedType);
    }

    @Override
    @NotNull
    protected Class<? extends Annotation> getNamedAnnotation() {
        return NamedRule.class;
    }
}
