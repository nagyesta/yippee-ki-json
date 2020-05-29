package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;

import java.io.File;
import java.util.Map;

/**
 * Calculates the file pairs needed for the transform operation using the input file
 * and the filtering parameters of the {@link RunConfig}.
 */
public interface FileSetTransformer {

    /**
     * Reads the IO specific properties of the {@link RunConfig} and generates the
     * file path for the output files for each matching input.
     *
     * @param runConfig The input configuration specifying the file sets.
     * @return A {@link Map} of {@link File}s where the input is the key and output
     * is the value.
     */
    Map<File, File> transformToFilePairs(RunConfig runConfig);
}
