package com.github.nagyesta.yippeekijson.core.config.parser.raw.params;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * {@link String} based implementation of {@link com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam}
 * supporting a collection of values.
 */
public final class RawConfigValueList extends BaseRawConfigParam<List<String>, List<String>> {

    public RawConfigValueList(@NotNull final String configPath, @NotNull final List<String> value) {
        super(configPath, Collections.unmodifiableList(List.copyOf(value)));
    }

    @NotNull
    @Override
    protected List<String> initConverted(@NotNull final List<String> list) {
        return list;
    }

    @Override
    public boolean isRepeated() {
        return true;
    }

    @Override
    public Collection<String> asStrings() {
        return getValue();
    }

}
