package com.github.nagyesta.yippeekijson.core.config.parser.raw.params;

import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Converter implementation capable of mapping raw data form the Yaml configuration to easier to process
 * {@link RawConfigParam} values.
 */
@Slf4j
public class RawParamConverter implements Function<Object, RawConfigParam> {

    private final String configKey;

    public RawParamConverter(@NonNull final String configKey) {
        Assert.hasText(configKey, "configKey cannot be blank.");
        this.configKey = configKey;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RawConfigParam apply(@NonNull final Object rawValue) {
        try {
            validateTypes(rawValue);
            RawConfigParam param;
            if (isSupportedValue(rawValue)) {
                param = asString(String.valueOf(rawValue));
            } else if (rawValue instanceof Map) {
                param = asMap((Map<String, Object>) rawValue);
            } else if (allInstanceOf((List<?>) rawValue, String.class)) {
                param = asList((List<String>) rawValue);
            } else {
                param = asListMap((List<Map<String, Object>>) rawValue);
            }
            return param;
        } catch (final Exception e) {
            final String message = "Failed to convert parameter: " + configKey + " due to: " + e.getMessage();
            log.error(message);
            throw new IllegalArgumentException(message, e);
        }
    }

    @NotNull
    private RawConfigValue asString(@NotNull final String rawValue) {
        return new RawConfigValue(configKey, rawValue);
    }

    @NotNull
    private RawConfigMap asMap(@NotNull final Map<String, Object> rawValue) {
        return new RawConfigMap(configKey, new TreeMap<>(rawValue));
    }

    @NotNull
    private RawConfigValueList asList(@NotNull final List<String> rawValue) {
        return new RawConfigValueList(configKey, rawValue);
    }

    @NotNull
    private RawConfigParam asListMap(@NotNull final List<Map<String, Object>> rawValue) {
        RawConfigParam param;
        param = new RawConfigMapList(configKey, rawValue.stream()
                .map(TreeMap::new)
                .collect(Collectors.toUnmodifiableList()));
        return param;
    }

    private void validateTypes(@NotNull final Object rawValue) {
        if (rawValue instanceof List) {
            if (isValidList(rawValue)) {
                throw new IllegalArgumentException("All parameter values must be using the same type (either String or Map).");
            }
            Assert.notEmpty((Collection<?>) rawValue, "Input collection cannot be empty.");
        } else if (rawValue instanceof Map) {
            Assert.notEmpty((Map<?, ?>) rawValue, "Input map cannot be empty.");
        } else if (!isSupportedValue(rawValue)) {
            throw new IllegalArgumentException("Parameter type is not compatible: " + rawValue.getClass());
        }
    }

    private boolean isSupportedValue(@NotNull final Object rawValue) {
        return rawValue instanceof String
                || rawValue instanceof Integer
                || rawValue instanceof Double
                || rawValue instanceof Boolean;
    }

    private boolean isValidList(@NotNull final Object rawValue) {
        return !allInstanceOf((List<?>) rawValue, String.class)
                && !allInstanceOf((List<?>) rawValue, Map.class);
    }

    private boolean allInstanceOf(@NotNull final List<?> rawValue, @NotNull final Class<?> aClass) {
        return rawValue.stream().allMatch(aClass::isInstance);
    }
}
