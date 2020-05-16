package com.github.nagyesta.yippeekijson.core.config.parser;

import com.github.nagyesta.yippeekijson.core.config.entities.JsonActions;

import java.io.InputStream;

/**
 * Parser for action configuration.
 */
public interface ActionConfigParser {

    /**
     * Does the parsing from a stream.
     *
     * @param stream The source of the configuration we need to parse.
     * @return The parsed configuration.
     * @throws RuntimeException When the parsing fails for some reason.
     * @implNote The stream will only be read but not closed. The caller, who opened the {@link InputStream} must make sure to close it.
     */
    JsonActions parse(InputStream stream) throws RuntimeException;
}
