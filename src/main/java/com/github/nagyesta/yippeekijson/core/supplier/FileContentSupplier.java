package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.exception.AbortTransformationException;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * {@link Supplier} returning the whole content of a {@link java.io.File}.
 */
@Slf4j
public final class FileContentSupplier implements Supplier<String> {

    static final String NAME = "file";

    private final String path;
    private final Charset charset;

    @SchemaDefinition(
            outputType = String.class,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_SUPPLIERS, section = "File content supplier"),
            sinceVersion = WikiConstants.VERSION_1_2_0,
            description = {
                    "This supplier returns the contents of a file as text."
            },
            example = @Example(
                    in = "/examples/json/validation-input.json",
                    out = "/examples/json/validation-output.json",
                    yml = "/examples/yml/validation.yml",
                    note = "In this example our file supplier provided the contents of the schema.")
    )
    @NamedSupplier(NAME)
    public FileContentSupplier(@ValueParam(docs = "The path of the file we want to use as input.")
                               @NonNull final String path,
                               @ValueParam(docs = "The charset used for reading the contents of the file. Defaults to UTF-8.")
                               @Nullable final Charset charset) {
        this.path = path;
        this.charset = Optional.ofNullable(charset)
                .orElse(StandardCharsets.UTF_8);
    }

    @Override
    public String get() {
        try {
            return FileUtils.readFileToString(new File(path), this.charset);
        } catch (final IOException e) {
            log.error("Failed to open file: '" + path + "' due to:" + e.getMessage(), e);
            throw new AbortTransformationException("Failed to open file: '" + path + "' due to:" + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FileContentSupplier.class.getSimpleName() + "[", "]")
                .add("path='" + path + "'")
                .add("charset='" + charset + "'")
                .toString();
    }
}
