package com.github.nagyesta.yippeekijson.metadata.schema.parser;

import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.DocumentationContext;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

/**
 * Wiki specific documentation parser.
 */
public class WikiMetadataParser {

    private static final String DASH = "-";
    private static final String HASH = "#";

    /**
     * Merges the data represented by an annotation instance into the builder we provide.
     *
     * @param source      The source annotation
     * @param destination The builder we want to merge into
     * @return The builder
     */
    @NotNull
    public DocumentationContext.DocumentationContextBuilder mergeInto(
            @NotNull final WikiLink source,
            @NotNull final DocumentationContext.DocumentationContextBuilder destination) {

        destination.wikiReference(Objects.requireNonNull(toUri(source)));
        return destination.fileName(StringUtils.trimToNull(source.file()))
                .sectionTitle(StringUtils.trimToNull(source.section()));
    }

    /**
     * Parses a {@link WikiLink} instance into a {@link URI}.
     *
     * @param source the annotation we want to parse.
     * @return the URI
     */
    @Nullable
    public URI toUri(@NotNull final WikiLink source) {
        if (StringUtils.isNotBlank(source.uri())) {
            return URI.create(source.uri());
        } else if (StringUtils.isNotBlank(source.file())) {
            return URI.create(buildWikiReference(source));
        } else {
            return null;
        }
    }

    @NotNull
    private String buildWikiReference(@NotNull final WikiLink source) {
        final String filePath = StringUtils.replace(FilenameUtils.removeExtension(source.file()), StringUtils.SPACE, DASH);
        final String section = Optional.ofNullable(StringUtils.trimToNull(source.section()))
                .map(StringUtils::lowerCase)
                .map(s -> HASH + StringUtils.replaceChars(s, StringUtils.SPACE, DASH))
                .orElse(StringUtils.EMPTY);
        return WikiConstants.CONTENT_ROOT + filePath + section;
    }

}
