package com.github.nagyesta.yippeekijson.core.config.parser;

import com.github.nagyesta.yippeekijson.core.config.entities.JsonActions;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;

import java.io.File;
import java.io.InputStream;

/**
 * Parser for action configuration.
 */
public interface ActionConfigParser {

    /**
     * Does the parsing from a stream.
     *
     * @param stream The source of the configuration we need to parse.
     * @param relaxed Turns off the strict schema validation
     * @return The parsed configuration.
     * @throws ConfigParseException When the parsing fails for some reason.
     * @implNote The stream will only be read but not closed. The caller, who opened the {@link InputStream} must make sure to close it.
     */
    JsonActions parse(InputStream stream, boolean relaxed) throws ConfigParseException;

    /**
     * Does the parsing from a file.
     *
     * @param config The source file containing the configuration we need to parse.
     * @param relaxed Turns off the strict schema validation
     * @return The parsed configuration.
     * @throws ConfigParseException When the parsing fails for some reason.
     */
    JsonActions parse(File config, boolean relaxed) throws ConfigParseException;
}
