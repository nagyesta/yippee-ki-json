package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.annotation.EmbedParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import com.github.nagyesta.yippeekijson.core.predicate.helper.MapSupport;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.*;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.typehelper.StringObjectMap;
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

    static final String PARAM_PREDICATE = "predicate";
    static final String NAME = "evalOn";
    static final String DELIMITER = ".";

    private final String childPath;
    private final Predicate<Object> wrappedPredicate;

    @SchemaDefinition(
            inputType = StringObjectMap.class,
            properties = @PropertyDefinitions(
                    @PropertyDefinition(name = PARAM_PREDICATE,
                            type = @TypeDefinition(itemType = Predicate.class, itemTypeParams = Object.class),
                            docs = "The Predicate we need to evaluate after the navigation is done."
                    )
            ),
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_PREDICATES, section = "Eval on predicate"),
            sinceVersion = WikiConstants.VERSION_1_1_0,
            description = {
                    "This predicate attempts to convert the input to a Map representation and attempts to navigate",
                    "using the keys of the Map as it is defined by the childPath parameter. The childPath parameter",
                    "is split using a single dot '.' as delimiter to find the small steps we need to take one after",
                    "the other. Once the navigation is done and reaches a valid path, we apply the predicate."
            },
            example = @Example(
                    in = "/examples/json/eval-on_in.json",
                    out = "/examples/json/eval-on_out.json",
                    yml = "/examples/yml/eval-on.yml",
                    note = {
                            "In this example we have used the eval on predicate to navigate to the \"zipCode\" of",
                            "the \"billing\" \"address\" and check whether it is numerical value to decide about",
                            "the removal of the \"address\" key on the account level."
                    })
    )
    @NamedPredicate(NAME)
    public EvalOnPredicate(
            @ValueParam(docs = "The desired navigation we want to perform relative to the Map/Object we operate on.")
            @javax.validation.constraints.Pattern(regexp = "^([$_a-zA-Z]+[$a-zA-Z0-9\\-_]+)(.[$_a-zA-Z]+[$a-zA-Z0-9\\-_]+)*$")
            @NonNull final String childPath,
            @EmbedParam @NonNull final Map<String, RawConfigParam> predicate,
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
