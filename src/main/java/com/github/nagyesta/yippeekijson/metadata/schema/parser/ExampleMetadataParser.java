package com.github.nagyesta.yippeekijson.metadata.schema.parser;

import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.DocumentationContext;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Example specific documentation parser.
 */
public class ExampleMetadataParser {

    /**
     * Merges the data represented by an annotation instance into the builder we provide.
     *
     * @param source      The source annotation
     * @param destination The builder we want to merge into
     * @return The builder
     */
    @NotNull
    public DocumentationContext.DocumentationContextBuilder mergeInto(
            @NotNull final Example source,
            @NotNull final DocumentationContext.DocumentationContextBuilder destination) {
        destination.exampleConfig(optionalResource(source.yml()))
                .exampleInput(optionalResource(source.in()))
                .exampleOutput(optionalResource(source.out()))
                .skipTest(source.skipTest());
        if (ArrayUtils.isNotEmpty(source.note())) {
            destination.exampleNote(Arrays.stream(source.note())
                    .map(StringUtils::trimToNull)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }
        return destination;
    }

    private Optional<ClassPathResource> optionalResource(@Nullable final String path) {
        Optional<ClassPathResource> optional = Optional.empty();
        if (StringUtils.isNotBlank(path)) {
            optional = Optional.of(new ClassPathResource(path));
        }
        return optional;
    }
}
