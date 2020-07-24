package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.predicate.helper.MapSupport;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.*;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.typehelper.StringObjectMap;
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

    @SchemaDefinition(
            inputType = StringObjectMap.class,
            properties = @PropertyDefinitions(
                    @PropertyDefinition(name = PARAM_KEY, commonTypeRef = "#/definitions/commonTypes/definitions/name")
            ),
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_PREDICATES, section = "Contains key predicate"),
            sinceVersion = WikiConstants.VERSION_1_1_0,
            description = {
                    "This predicate observes the input as a Map and returns true if it has a key named exactly",
                    "matching the value of the key."
            },
            example = @Example(
                    in = "/examples/json/delete-from-account_in.json",
                    out = "/examples/json/delete-from-account_out.json",
                    yml = "/examples/yml/delete-from.yml",
                    note = {
                            "In this example we have used the contains key predicate to allow the rule execution",
                            "only in case the account has an address field."
                    })
    )
    @NamedPredicate(NAME)
    public ContainsKeyPredicate(@ValueParam(docs = "The name of the key we want to check.")
                                @NonNull final String key) {
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
