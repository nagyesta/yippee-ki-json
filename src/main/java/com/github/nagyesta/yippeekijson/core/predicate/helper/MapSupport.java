package com.github.nagyesta.yippeekijson.core.predicate.helper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * Provides support for {@link Map} conversion and null-checks.
 */
public class MapSupport {

    /**
     * Casts an {@link Object} to {@link Map} if it is assignable.
     *
     * @param object The input object
     * @return an {@link Optional} with the {@link Map} in it if the cast was possible,
     */
    @SuppressWarnings("unchecked")
    @NotNull
    protected Optional<Map<String, Object>> toOptionalMap(@Nullable final Object object) {
        if (!(object instanceof Map)) {
            return Optional.empty();
        }
        return Optional.of((Map<String, Object>) object);
    }
}
