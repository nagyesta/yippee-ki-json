package com.github.nagyesta.yippeekijson.metadata.schema.parser;

import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.DocumentationContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.DocumentationContext.DocumentationContextBuilder;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.typehelper.ParametrizedTypeAware;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

/**
 * Parser for {@link DocumentationContext} properties.
 */
public class DocumentationMetadataParser {

    private final WikiMetadataParser wikiParser;
    private final ExampleMetadataParser exampleParser;

    public DocumentationMetadataParser(@NotNull final WikiMetadataParser wikiParser,
                                       @NotNull final ExampleMetadataParser exampleParser) {
        this.wikiParser = wikiParser;
        this.exampleParser = exampleParser;
    }

    /**
     * Parses the documentation specific section of the {@link SchemaDefinition}.
     *
     * @param source the annotation to be parsed
     * @return the parsed documentation
     */
    public DocumentationContext parse(@NotNull final SchemaDefinition source) {
        final DocumentationContextBuilder builder = exampleParser
                .mergeInto(source.example(), DocumentationContext.builder());
        return wikiParser.mergeInto(source.wikiLink(), builder)
                .sinceVersion(StringUtils.trimToNull(source.sinceVersion()))
                .inputType(optionalType(source.inputType()))
                .outputType(optionalType(source.outputType()))
                .description(Arrays.asList(source.description()))
                .build();
    }

    @NotNull
    private Optional<Type> optionalType(@NotNull final Class<?> type) {
        Optional<Type> optional = Optional.empty();
        if (ParametrizedTypeAware.KNOWN_TYPES.containsKey(type)) {
            optional = Optional.of(ParametrizedTypeAware.KNOWN_TYPES.get(type));
        } else if (!Void.class.equals(type)) {
            optional = Optional.of(type);
        }
        return optional;
    }
}
