package com.github.nagyesta.yippeekijson.core.config.parser.raw.params;

import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link Map} based implementation of {@link RawConfigParam} supporting a collection of values.
 */
public final class RawConfigMapList extends BaseRawConfigParam<List<Map<String, Object>>, List<Map<String, RawConfigParam>>> {

    public RawConfigMapList(@NotNull final String configPath, @NotNull final List<Map<String, Object>> value) {
        super(configPath, value.stream()
                .map(Map::copyOf)
                .map(Collections::unmodifiableMap)
                .collect(Collectors.toUnmodifiableList()));
    }

    @NotNull
    @Override
    protected List<Map<String, RawConfigParam>> initConverted(@NotNull final List<Map<String, Object>> maps) {
        return maps.stream()
                .map(item -> item.entrySet().stream()
                        .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, this::convertValueOfEntry)))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean isRepeated() {
        return true;
    }

    @Override
    public boolean isMapType() {
        return true;
    }

    @Override
    public Collection<Map<String, RawConfigParam>> asMaps() {
        return getConverted();
    }

}
