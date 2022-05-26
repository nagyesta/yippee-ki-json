package com.github.nagyesta.yippeekijson.metadata.schema.definitions.registry;

import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaObject;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.NamedJsonSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.CommonStringValuesType;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.NamedComponentConverter;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonCompositionSchemaTypeDefinitionImpl;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonConstantSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonIfElseSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonPropertiesSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import com.github.nagyesta.yippeekijson.metadata.schema.parser.ComponentContextMetadataParser;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.nagyesta.yippeekijson.core.NamedComponentUtil.findAnnotatedConstructorOfNamedComponent;
import static com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.NamedComponentConverter.PROPERTY_NAME;
import static com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonRefSchemaTypeDefinition.ref;

public abstract class AbstractComponentTypeDefinitionRegistry {

    /**
     * Cache name for the registry caching.
     */
    protected static final String JSON_TYPES = "jsonTypes";
    private final Map<String, JsonSchemaTypeDefinition> definitions = new TreeMap<>();
    private final Map<String, String> nameConstants = new TreeMap<>();
    private final ComponentContextMetadataParser componentContextMetadataParser;
    private final NamedComponentConverter namedComponentConverter;
    private final Set<ComponentContext> knownContexts = new HashSet<>();

    protected AbstractComponentTypeDefinitionRegistry(@NotNull final ComponentContextMetadataParser componentContextMetadataParser,
                                                      @NotNull final NamedComponentConverter namedComponentConverter) {
        this.componentContextMetadataParser = componentContextMetadataParser;
        this.namedComponentConverter = namedComponentConverter;
    }

    @NotNull
    protected abstract String appendPrefix(@NotNull String name);

    /**
     * Combines component definitions into a composite type (e.g. anyType).
     *
     * @param names THe names of the component we will combine
     * @return the combined type
     */
    @NotNull
    protected JsonCompositionSchemaTypeDefinitionImpl combinedComponentDefinitions(final Set<String> names) {
        List<JsonSchemaObject> list = new ArrayList<>();
        list.add(nameRestrictionEnum(names.stream()
                .map(this.nameConstants::get)
                .collect(Collectors.toSet())));
        list.addAll(Arrays.stream(ruleDefinitionReferenceSwitch(names)).distinct().collect(Collectors.toList()));
        return JsonCompositionSchemaTypeDefinitionImpl.builder()
                .allOf(list)
                .build();
    }

    /**
     * Provides access to the definitions held by this object.
     *
     * @return definitions.
     */
    @NotNull
    protected Map<String, JsonSchemaTypeDefinition> definitions() {
        if (definitions.isEmpty()) {
            this.initDefinitions();
        }
        return this.definitions;
    }

    /**
     * Allows us easy parsing and registering for named types.
     *
     * @param namedType The named type we need to register
     * @return the parsed {@link ComponentContext}.
     */
    protected ComponentContext parseAndRegister(@NotNull final Class<?> namedType) {
        final Optional<Constructor<?>> component = findAnnotatedConstructorOfNamedComponent(namedType, getNamedAnnotation());
        final ComponentContext context = componentContextMetadataParser.parse(component
                .orElseThrow(() -> new IllegalArgumentException("Constructor not found: " + namedType.getName())));
        this.knownContexts.add(context);
        return context;
    }

    /**
     * Allows us to generate an enum from the known named component names.
     *
     * @param names The names of the components.
     * @return enum node
     */
    @NotNull
    protected JsonPropertiesSchemaTypeDefinition nameRestrictionEnum(@NotNull final Set<String> names) {
        return JsonPropertiesSchemaTypeDefinition.builder()
                .addRequiredProperty(PROPERTY_NAME, CommonStringValuesType.builder()
                        .addEnum(names.toArray(new String[0]))
                        .build())
                .build();
    }

    @NotNull
    protected abstract Class<? extends Annotation> getNamedAnnotation();

    private void initDefinitions() {
        this.knownContexts.forEach(this::processContext);
    }

    private void processContext(@NotNull final ComponentContext context) {
        final NamedJsonSchemaTypeDefinition typeDefinition = namedComponentConverter.convert(context);
        Assert.isTrue(!definitions.containsKey(typeDefinition.getJsonTypeDefinitionName()),
                "Type is already registered with name: " + typeDefinition.getJsonTypeDefinitionName());
        definitions.put(typeDefinition.getJsonTypeDefinitionName(), typeDefinition);
        nameConstants.put(typeDefinition.getJsonTypeDefinitionName(), context.getComponentName());
    }

    @NotNull
    private JsonSchemaObject[] ruleDefinitionReferenceSwitch(@NotNull final Set<String> names) {
        return names.stream()
                .map(name -> JsonIfElseSchemaTypeDefinition.builder()
                        .ifNode(JsonPropertiesSchemaTypeDefinition.builder()
                                .addProperty(PROPERTY_NAME, JsonConstantSchemaTypeDefinition.builder()
                                        .constant(this.nameConstants.get(name))
                                        .build())
                                .build())
                        .thenNode(ref(appendPrefix(name)))
                        .build())
                .toArray(JsonSchemaObject[]::new);
    }

}
