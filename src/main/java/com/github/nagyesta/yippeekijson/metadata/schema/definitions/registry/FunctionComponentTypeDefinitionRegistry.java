package com.github.nagyesta.yippeekijson.metadata.schema.definitions.registry;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonNamedComponentTypeDefinitionRegistry;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.NamedComponentConverter;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonCompositionSchemaTypeDefinitionImpl;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.DocumentationContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.typehelper.ParametrizedTypeAware;
import com.github.nagyesta.yippeekijson.metadata.schema.parser.ComponentContextMetadataParser;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;

import static com.github.nagyesta.yippeekijson.core.NamedComponentUtil.translateKnownType;

public class FunctionComponentTypeDefinitionRegistry extends AbstractComponentTypeDefinitionRegistry
        implements JsonNamedComponentTypeDefinitionRegistry<Function<?, ?>> {

    private static final String TYPE_DEFINITIONS_ROOT = "#/definitions/" + FUNCTION_TYPES + "/definitions/";
    private static final String ANY_TYPE = "anyFunctionType";
    private static final String CACHE_KEY = "'functionDefinitions'";
    private static final String TYPE_NAME_MESSAGE_FORMAT = "{0}To{1}FunctionType";

    private final Map<Type, Map<Type, Set<String>>> inputOutputPairToRefMap = new TreeMap<>(Comparator.comparing(Type::getTypeName));
    private final Map<Type, Map<Type, String>> inputOutputPairToTypeNameMap = new TreeMap<>(Comparator.comparing(Type::getTypeName));

    public FunctionComponentTypeDefinitionRegistry(@NotNull final ComponentContextMetadataParser componentContextMetadataParser,
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
        //noinspection CodeBlock2Expr
        inputOutputPairToTypeNameMap.forEach((inputType, map) -> {
            map.forEach((outputType, typeName) -> {
                Set<String> typeRefs = inputOutputPairToRefMap
                        .getOrDefault(inputType, new TreeMap<>(Comparator.comparing(Type::getTypeName)))
                        .getOrDefault(outputType, new TreeSet<>());
                // include the functions converting from the same input but returning Object
                // some of these just cannot give more strict output types as it is dynamic
                if (Object.class.equals(outputType)) {
                    inputOutputPairToRefMap
                            .getOrDefault(inputType, new TreeMap<>(Comparator.comparing(Type::getTypeName)))
                            .forEach((out, refs) -> typeRefs.addAll(refs));
                }
                cachedMap.put(typeName, combinedComponentDefinitions(typeRefs));
            });
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
        if (inputOutputPairToTypeNameMap.getOrDefault(inputType, Map.of()).containsKey(outputType)) {
            return Optional.of(inputOutputPairToTypeNameMap
                    .getOrDefault(inputType, Map.of()).get(outputType))
                    .map(this::appendPrefix);
        }
        return Optional.of(ANY_TYPE).map(this::appendPrefix);
    }

    @Override
    @CacheEvict(cacheNames = JSON_TYPES, key = CACHE_KEY)
    public void registerType(@NotNull final Class<? extends Function<?, ?>> namedType) {
        final ComponentContext componentContext = parseAndRegister(namedType);
        final Optional<DocumentationContext> documentation = Optional.ofNullable(componentContext.getDocumentation());
        final Type input = translateKnownType(documentation
                .flatMap(DocumentationContext::getInputType)
                .orElse(Object.class));
        final Type output = translateKnownType(documentation
                .flatMap(DocumentationContext::getOutputType)
                .orElse(Object.class));
        registerToRefTypes(input, output, componentContext.getJsonTypeName());
        documentation.ifPresent((ignored) -> registerToKnownTypeNames(input, output));
    }

    private void registerToRefTypes(@NotNull final Type input,
                                    @NotNull final Type output,
                                    @NotNull final String jsonTypeName) {
        this.inputOutputPairToRefMap.computeIfAbsent(input, ignored -> new HashMap<>())
                .computeIfAbsent(output, ignored -> new TreeSet<>())
                .add(jsonTypeName);
    }

    private void registerToKnownTypeNames(@NotNull final Type input,
                                          @NotNull final Type output) {
        String inputName = StringUtils.uncapitalize(ParametrizedTypeAware.TYPE_NAME_TRANSLATION.get(input));
        String outputName = ParametrizedTypeAware.TYPE_NAME_TRANSLATION.get(output);
        this.inputOutputPairToTypeNameMap.computeIfAbsent(input, ignored -> new HashMap<>())
                .computeIfAbsent(output, ignored -> MessageFormat.format(TYPE_NAME_MESSAGE_FORMAT, inputName, outputName));
    }

    @Override
    @NotNull
    protected Class<? extends Annotation> getNamedAnnotation() {
        return NamedFunction.class;
    }
}
