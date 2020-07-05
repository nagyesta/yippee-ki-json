package com.github.nagyesta.yippeekijson.metadata.schema.definitions.registry;

import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonNamedComponentTypeDefinitionRegistry;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.NamedComponentConverter;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonCompositionSchemaTypeDefinitionImpl;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.DocumentationContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.typehelper.ParametrizedTypeAware;
import com.github.nagyesta.yippeekijson.metadata.schema.parser.ComponentContextMetadataParser;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Predicate;

import static com.github.nagyesta.yippeekijson.core.NamedComponentUtil.translateKnownType;

public class PredicateComponentTypeDefinitionRegistry extends AbstractComponentTypeDefinitionRegistry
        implements JsonNamedComponentTypeDefinitionRegistry<Predicate<?>> {

    private static final String TYPE_DEFINITIONS_ROOT = "#/definitions/" + PREDICATE_TYPES + "/definitions/";
    private static final String ANY_TYPE = "anyPredicateType";
    private static final String CACHE_KEY = "'predicateDefinitions'";
    private static final String TYPE_NAME_MESSAGE_FORMAT = "{0}PredicateType";

    private final Map<Type, Set<String>> inputToRefMap = new TreeMap<>(Comparator.comparing(Type::getTypeName));
    private final Map<Type, String> inputToTypeNameMap = new TreeMap<>(Comparator.comparing(Type::getTypeName));

    public PredicateComponentTypeDefinitionRegistry(@NotNull final ComponentContextMetadataParser componentContextMetadataParser,
                                                    @NotNull final NamedComponentConverter namedComponentConverter) {
        super(componentContextMetadataParser, namedComponentConverter);
    }

    @Override
    @Cacheable(cacheNames = JSON_TYPES, key = CACHE_KEY)
    public @NotNull Map<String, JsonSchemaTypeDefinition> registeredDefinitions() {
        final LinkedHashMap<String, JsonSchemaTypeDefinition> cachedMap = new LinkedHashMap<>(definitions());
        putTypeRefs(cachedMap);
        cachedMap.put(ANY_TYPE, anyTypeDefinition());
        return cachedMap;
    }

    private void putTypeRefs(final LinkedHashMap<String, JsonSchemaTypeDefinition> cachedMap) {
        inputToTypeNameMap.forEach((inputType, typeName) -> {
            // include the Object predicates as well to not reduce functionality
            // e.g. notNull can be a valid String predicate
            Set<String> typeRefs = Sets.union(
                    inputToRefMap.getOrDefault(inputType, Set.of()),
                    inputToRefMap.getOrDefault(Object.class, Set.of())
            );
            cachedMap.put(typeName, combinedComponentDefinitions(typeRefs));
        });
    }

    @NotNull
    private JsonCompositionSchemaTypeDefinitionImpl anyTypeDefinition() {
        return combinedComponentDefinitions(definitions().keySet());
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
    @NotNull
    protected Class<? extends Annotation> getNamedAnnotation() {
        return NamedPredicate.class;
    }

    @Override
    @CacheEvict(cacheNames = JSON_TYPES, key = CACHE_KEY)
    public void registerType(@NotNull final Class<? extends Predicate<?>> namedType) {
        final ComponentContext componentContext = parseAndRegister(namedType);
        final Type input = translateKnownType(Optional.ofNullable(componentContext.getDocumentation())
                .flatMap(DocumentationContext::getInputType)
                .orElse(Object.class));
        registerToRefTypes(input, componentContext.getJsonTypeName());
        registerToKnownTypeNames(input);
    }

    private void registerToRefTypes(@NotNull final Type input,
                                    @NotNull final String jsonTypeName) {
        this.inputToRefMap.computeIfAbsent(input, ignored -> new TreeSet<>())
                .add(jsonTypeName);
    }

    private void registerToKnownTypeNames(@NotNull final Type input) {
        if (Object.class.equals(input)) {
            return;
        }
        String inputName = StringUtils.uncapitalize(ParametrizedTypeAware.TYPE_NAME_TRANSLATION.get(input));
        this.inputToTypeNameMap.computeIfAbsent(input, ignored -> MessageFormat.format(TYPE_NAME_MESSAGE_FORMAT, inputName));
    }
}
