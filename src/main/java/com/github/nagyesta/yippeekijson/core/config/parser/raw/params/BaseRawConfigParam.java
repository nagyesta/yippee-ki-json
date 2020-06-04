package com.github.nagyesta.yippeekijson.core.config.parser.raw.params;

import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Base config parameter implementation handling most of the conversion and processing for {@link RawConfigParam} types.
 *
 * @param <T> The input type of the values coming from the Yaml.
 * @param <R> The target type converted as the implementing type needs it.
 */
public abstract class BaseRawConfigParam<T, R> implements RawConfigParam {

    private final String configPath;
    private final T value;
    private final R converted;

    protected BaseRawConfigParam(@NotNull final String configPath,
                                 @NotNull final T value) {
        this.configPath = configPath;
        this.value = value;
        this.converted = initConverted(value);
    }

    protected abstract R initConverted(@NotNull T t);

    @Override
    public boolean isRepeated() {
        return false;
    }

    @Override
    public boolean isMapType() {
        return false;
    }

    @NotNull
    @Override
    public Object suitableFor(final boolean stringMap, final boolean paramMap, final boolean repeat) {
        Assert.isTrue(paramMap == stringMap || !paramMap,
                "Param maps cannot be used when maps are not selected either: " + configPath);
        Assert.isTrue(repeat == isRepeated(),
                "Repeated value requirement needs to match param for: " + configPath);
        Assert.isTrue(stringMap == isMapType(),
                "Map value requirement needs to match param for: " + configPath);
        Object result;
        if (repeat) {
            result = getCollectionValue(stringMap, paramMap);
        } else {
            result = getSingleValue(stringMap, paramMap);
        }
        return result;
    }

    @NotNull
    private Object getSingleValue(final boolean stringMap, final boolean paramMap) {
        Object result;
        if (paramMap) {
            result = this.asMap();
        } else if (stringMap) {
            result = this.unwrap(this.asMap());
        } else {
            result = this.asString();
        }
        return result;
    }

    private Collection<?> getCollectionValue(final boolean stringMap, final boolean paramMap) {
        Collection<?> result;
        if (paramMap) {
            result = this.asMaps();
        } else if (stringMap) {
            result = this.asMaps().stream()
                    .map(this::unwrap)
                    .collect(Collectors.toUnmodifiableList());
        } else {
            result = this.asStrings();
        }
        return result;
    }

    @NotNull
    @Override
    public String getConfigPath() {
        return configPath;
    }

    /**
     * Converts the value of the provided {@link java.util.Map.Entry} to a {@link RawConfigParam}.
     *
     * @param entry the source we need to convert
     * @return the converted parameter value
     */
    @NotNull
    protected RawConfigParam convertValueOfEntry(@NotNull final Map.Entry<String, Object> entry) {
        return new RawParamConverter(childPathOf(entry.getKey())).apply(entry.getValue());
    }

    @NotNull
    private Map<String, String> unwrap(@NotNull final Map<String, RawConfigParam> item) {
        return item.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> e.getValue().asString()));
    }

    @NotNull
    private String childPathOf(@NotNull final String key) {
        return configPath + "." + key;
    }

    /**
     * Provides access to the original value received when the constructor was called.
     *
     * @return The original value stored by this parameter.
     */
    @NotNull
    protected T getValue() {
        return value;
    }

    /**
     * Provides access to the converted value calculated form the original.
     *
     * @return The converted value stored by this parameter.
     */
    @NotNull
    protected R getConverted() {
        return converted;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("value=" + value)
                .add("converted=" + converted)
                .toString();
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(this.getClass().isInstance(o))) {
            return false;
        }

        return Boolean.logicalAnd(Objects.equals(value, this.getClass().cast(o).getValue()),
                Objects.equals(configPath, this.getClass().cast(o).getConfigPath()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, this.configPath);
    }
}
