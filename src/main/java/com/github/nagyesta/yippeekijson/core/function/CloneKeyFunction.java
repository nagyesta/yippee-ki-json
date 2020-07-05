package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.*;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.typehelper.StringObjectMap;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * {@link Function} for cloning a key in the context of a {@link Map} and putting it using a new key.
 */
@Slf4j
public final class CloneKeyFunction implements Function<Map<String, Object>, Map<String, Object>> {

    static final String NAME = "cloneKey";
    static final String PARAM_FROM = "from";
    static final String PARAM_TO = "to";
    private final String from;
    private final String to;

    @SchemaDefinition(
            inputType = StringObjectMap.class,
            outputType = StringObjectMap.class,
            properties = @PropertyDefinitions({
                    @PropertyDefinition(name = PARAM_FROM, commonTypeRef = "#/definitions/commonTypes/definitions/name"),
                    @PropertyDefinition(name = PARAM_TO, commonTypeRef = "#/definitions/commonTypes/definitions/name")
            }),
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_FUNCTIONS, section = "Clone key function"),
            sinceVersion = WikiConstants.VERSION_1_1_0,
            description = {
                    "This function takes the input value as a Map and puts the value assigned to the \"from\" key using the \"to\" key."
            },
            example = @Example(
                    in = "/examples/json/account_replace-map_in.json",
                    out = "/examples/json/account_replace-map_out.json",
                    yml = "/examples/yml/replace-map.yml",
                    note = "This example clones \"billingAddress\" to \"shippingAddress\" thanks to this function."
            )
    )
    @NamedFunction(NAME)
    public CloneKeyFunction(@ValueParam(docs = "The name of the key we need to duplicate.")
                            @NonNull final String from,
                            @ValueParam(docs = "The name of the destination key.")
                            @NonNull final String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Map<String, Object> apply(final Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        final Map<String, Object> result = new TreeMap<>(map);
        if (result.containsKey(to)) {
            log.warn(String.format("Key to: \"%s\" already present in input and will be overwritten.", to));
        }
        if (!map.containsKey(from)) {
            log.warn(String.format("Key from: \"%s\" not found in input. Skipping clone operation.", from));
        } else {
            result.put(to, map.get(from));
        }
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CloneKeyFunction.class.getSimpleName() + "[", "]")
                .add("from='" + from + "'")
                .add("to='" + to + "'")
                .toString();
    }
}
