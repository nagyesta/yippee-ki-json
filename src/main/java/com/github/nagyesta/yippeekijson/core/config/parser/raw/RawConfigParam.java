package com.github.nagyesta.yippeekijson.core.config.parser.raw;

import com.github.nagyesta.yippeekijson.core.config.parser.impl.ParameterContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Defines common behavior of parsed configuration parameters.
 */
public interface RawConfigParam {

    /**
     * Returns the config path identifying the current parameter in the Yaml.
     *
     * @return the path where this parameter can be found.
     */
    @NotNull
    String getConfigPath();

    /**
     * Tells the caller whether the current type is {@link Collection} based implementation.
     *
     * @return true if {@link Collection} false otherwise
     */
    boolean isRepeated();

    /**
     * Tells the caller whether the current type is {@link Map} based implementation.
     *
     * @return true if {@link Map} false otherwise
     */
    boolean isMapType();

    /**
     * Returns the value as a single {@link String} (if supported).
     *
     * @return value
     */
    default String asString() {
        throw new UnsupportedOperationException("Conversion is not supported from: " + this.getClass());
    }

    /**
     * Returns the value as a {@link Collection} of {@link String} values (if supported).
     *
     * @return value
     */
    default Collection<String> asStrings() {
        return Collections.singletonList(asString());
    }

    /**
     * Returns the value as a single {@link Map} (if supported).
     *
     * @return value
     */
    default Map<String, RawConfigParam> asMap() {
        throw new UnsupportedOperationException("Conversion is not supported from: " + this.getClass());
    }

    /**
     * Returns the value as a {@link Collection} of {@link Map} values (if supported).
     *
     * @return value
     */
    default Collection<Map<String, RawConfigParam>> asMaps() {
        return Collections.singletonList(asMap());
    }

    /**
     * Returns the value as a single {@link String} {@link Map} (if supported).
     *
     * @return value
     */
    default Map<String, String> asStringMap() {
        throw new UnsupportedOperationException("Conversion is not supported from: " + this.getClass());
    }

    /**
     * Returns the value as a {@link Collection} of {@link String} {@link Map} values (if supported).
     *
     * @return value
     */
    default Collection<Map<String, String>> asStringMaps() {
        return Collections.singletonList(asStringMap());
    }

    /**
     * Finds the suitable representation based on the input parameters and the existing values.
     *
     * @param context The context where we want to use the configuration values.
     * @return the converted config
     */
    @NotNull
    Object suitableFor(ParameterContext context);
}
