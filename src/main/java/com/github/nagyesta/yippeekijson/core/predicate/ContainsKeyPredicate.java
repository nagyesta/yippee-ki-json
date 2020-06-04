package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.annotation.MethodParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.core.predicate.helper.MapSupport;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Predicate;

/**
 * {@link Predicate} matching any {@link Map} which contains the given key.
 */
public final class ContainsKeyPredicate extends MapSupport implements Predicate<Object> {

    static final String NAME = "containsKey";
    static final String PARAM_KEY = "key";

    private final String key;

    @NamedPredicate(NAME)
    public ContainsKeyPredicate(@MethodParam(PARAM_KEY) @NonNull final String key) {
        this.key = key;
    }

    @Override
    public boolean test(@Nullable final Object object) {
        Optional<Map<String, Object>> stringObjectMap = toOptionalMap(object);
        return stringObjectMap.map(o -> o.containsKey(key)).orElse(false);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ContainsKeyPredicate.class.getSimpleName() + "[", "]")
                .add("key=" + key)
                .toString();
    }
}
