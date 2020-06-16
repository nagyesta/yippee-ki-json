package com.github.nagyesta.yippeekijson.core.config.parser.raw.params;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

/**
 * {@link String} based implementation of {@link com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam}
 * supporting a single value.
 */
public final class RawConfigValue extends BaseRawConfigParam<String, String> {

    public RawConfigValue(@NotNull final String configPath, @NotNull final String value) {
        super(configPath, value);
    }

    @NotNull
    @Override
    protected String initConverted(@NotNull final String s) {
        return s;
    }

    @Override
    public String asString() {
        return getValue();
    }

    @Override
    public Collection<String> asStrings() {
        return Collections.singletonList(getValue());
    }
}
