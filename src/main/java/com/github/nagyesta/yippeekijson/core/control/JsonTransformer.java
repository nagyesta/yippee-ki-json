package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.JsonAction;
import com.github.nagyesta.yippeekijson.core.exception.JsonTransformException;

import java.io.File;
import java.io.InputStream;

/**
 * Applies the provided actions to the JSON streams.
 */
public interface JsonTransformer {

    /**
     * Applies the rules from the action parameter to the JSON file on the input stream.
     *
     * @param json   The input stream.
     * @param action The action to be applied.
     * @return The pretty-printed String of the transformed JSON.
     * @throws JsonTransformException When the transform operation is not possible.
     */
    String transform(InputStream json, JsonAction action) throws JsonTransformException;

    /**
     * Applies the rules from the action parameter to the JSON file.
     *
     * @param json   The input file.
     * @param action The action to be applied.
     * @return The pretty-printed String of the transformed JSON.
     * @throws JsonTransformException When the transform operation is not possible.
     */
    String transform(File json, JsonAction action) throws JsonTransformException;
}
