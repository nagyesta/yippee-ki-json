package com.github.nagyesta.yippeekijson.metadata.schema.parser;

import com.github.nagyesta.yippeekijson.metadata.schema.annotation.PropertyDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.TypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.PropertyContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.typehelper.ParametrizedTypeAware;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.Assert;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Parser processing {@link SchemaDefinition} annotations.
 */
public class SchemaDefinitionMetadataParser {

    private final DocumentationMetadataParser documentationParser;

    public SchemaDefinitionMetadataParser(@NotNull final DocumentationMetadataParser documentationParser) {
        this.documentationParser = documentationParser;
    }

    /**
     * Parses the provided annotation and merges the results into the provided builder.
     *
     * @param source      The annotation
     * @param destination The builder
     * @return builder
     */
    @SuppressWarnings("UnusedReturnValue")
    public ComponentContext.ComponentContextBuilder mergeInto(@NotNull final SchemaDefinition source,
                                                              @NotNull final ComponentContext.ComponentContextBuilder destination) {
        if (StringUtils.isNotBlank(source.typeName())) {
            destination.jsonTypeName(source.typeName());
        }
        destination.pathRestriction(
                StringUtils.trimToNull(source.pathRestriction().constant()),
                StringUtils.trimToNull(source.pathRestriction().docs()));
        if (ArrayUtils.isNotEmpty(source.properties().value())) {
            Arrays.stream(source.properties().value())
                    .forEach(propertyDefinition -> {
                        PropertyContext context = parse(Arrays.asList(propertyDefinition.name()), propertyDefinition);
                        destination.propertiesMerge(propertyDefinition.name(), context);
                    });
        }
        return destination.allowedProperties(source.properties().minProperties(), source.properties().maxProperties())
                .documentation(documentationParser.parse(source));
    }

    @NotNull
    private PropertyContext parse(@NotNull final List<String> names,
                                  @NotNull final PropertyDefinition source) {
        PropertyContext result;
        if (names.size() > 1) {
            result = PropertyContext.builder()
                    .name(names.get(0))
                    .child(Optional.of(parse(names.subList(1, names.size()), source)))
                    .docs(Optional.of("Wrapper object."))
                    .build();
        } else {
            result = PropertyContext.builder()
                    .child(Optional.empty())
                    .docs(Optional.ofNullable(StringUtils.trimToNull(source.docs())))
                    .commonTypeRef(Optional.ofNullable(StringUtils.trimToNull(source.commonTypeRef())))
                    .name(names.get(0))
                    .required(source.required())
                    .type(toType(source.type()))
                    .build();
        }
        return result;
    }

    @Nullable
    private Type toType(final TypeDefinition type) {
        final Class<?> itemType = type.itemType();
        final Class<?>[] itemTypeParams = type.itemTypeParams();
        if (Void.class.equals(itemType)) {
            return null;
        }
        Type result = itemType;
        if (ParametrizedTypeAware.KNOWN_TYPES.containsKey(itemType)) {
            result = ParametrizedTypeAware.KNOWN_TYPES.get(itemType);
        } else if (ArrayUtils.isNotEmpty(itemTypeParams)) {
            result = TypeUtils.parameterize(itemType, processTypeParameters(itemTypeParams));
        }
        if (type.isCollection()) {
            result = TypeUtils.parameterize(Collection.class, result);
        }
        return result;
    }

    @NotNull
    private Type[] processTypeParameters(@NotNull final Class<?>[] itemTypeParams) {
        return Arrays.stream(itemTypeParams)
                .map(classObj -> {
                    final Type result = ParametrizedTypeAware.KNOWN_TYPES.getOrDefault(classObj, classObj);
                    Assert.isTrue(TypeUtils.isAssignable(classObj, result), "Type is not assignable from: "
                            + classObj.getName() + " to: " + result.getTypeName());
                    return result;
                })
                .collect(Collectors.toList())
                .toArray(new Type[itemTypeParams.length]);
    }

}
