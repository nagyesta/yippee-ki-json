package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.exception.AbortTransformationException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nullable;
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

    @NamedSupplier(NAME)
    public FileContentSupplier(@ValueParam @NonNull final String path,
                               @ValueParam @Nullable final String charset) {
        this.path = path;
        this.charset = Optional.ofNullable(charset)
                .map(Charset::forName)
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
