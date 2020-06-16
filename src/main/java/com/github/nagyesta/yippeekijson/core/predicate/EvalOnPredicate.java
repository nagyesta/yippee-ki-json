package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.annotation.MethodParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import com.github.nagyesta.yippeekijson.core.predicate.helper.MapSupport;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * {@link Predicate} matching any {@link Map} which contains the given key.
 */
public final class EvalOnPredicate extends MapSupport implements Predicate<Object> {

    static final String NAME = "evalOn";
    static final String PARAM_CHILD_PATH = "childPath";
    static final String PARAM_PREDICATE = "predicate";
    static final String DELIMITER = ".";

    private final String childPath;
    private final Predicate<Object> wrappedPredicate;

    @NamedPredicate(NAME)
    public EvalOnPredicate(@MethodParam(PARAM_CHILD_PATH) @NonNull final String childPath,
                           @MethodParam(value = PARAM_PREDICATE, stringMap = true, paramMap = true)
                           @NonNull final Map<String, RawConfigParam> predicate,
                           @NonNull final FunctionRegistry functionRegistry) {
        this.childPath = childPath;
        this.wrappedPredicate = functionRegistry.lookupPredicate(predicate);
    }

    @Override
    public boolean test(@Nullable final Object object) {
        Optional<?> optional = toOptionalMap(object);
        for (final String child : Objects.requireNonNull(childPath.split(Pattern.quote(DELIMITER)))) {
            optional = optional.map(o -> {
                if (o instanceof Map) {
                    return ((Map<?, ?>) o).get(child);
                } else {
                    return null;
                }
            });
        }
        return wrappedPredicate.test(optional.orElse(null));
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EvalOnPredicate.class.getSimpleName() + "[", "]")
                .add("childPath='" + childPath + "'")
                .add("wrappedPredicate=" + wrappedPredicate)
                .toString();
    }
}
