package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.MethodParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
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

    @NamedFunction(NAME)
    public CloneKeyFunction(@MethodParam(PARAM_FROM) @NonNull final String from,
                            @MethodParam(PARAM_TO) @NonNull final String to) {
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
