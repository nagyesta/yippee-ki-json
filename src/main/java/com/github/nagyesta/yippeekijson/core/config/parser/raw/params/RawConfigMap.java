package com.github.nagyesta.yippeekijson.core.config.parser.raw.params;

import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link Map} based implementation of {@link RawConfigParam} supporting a single value.
 */
public final class RawConfigMap extends BaseRawConfigParam<Map<String, Object>, Map<String, RawConfigParam>> {

    public RawConfigMap(@NotNull final String configPath, @NotNull final Map<String, Object> value) {
        super(configPath, Collections.unmodifiableMap(Map.copyOf(value)));
    }

    @Override
    public boolean isMapType() {
        return true;
    }

    @Override
    public Map<String, RawConfigParam> asMap() {
        return getConverted();
    }

    @NotNull
    @Override
    protected Map<String, RawConfigParam> initConverted(@NotNull final Map<String, Object> value) {
        return value.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, this::convertValueOfEntry));
    }

}
