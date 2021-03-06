package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.impl.helper.JsonMapRuleSupport;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.*;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.typehelper.StringObjectMap;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A filtering rule operating on the fields of an Object at {@link com.jayway.jsonpath.JsonPath}.
 * The node identified by the path must be convertible to a {@link Map} and matching a {@link Predicate}.
 * Deletion can be based on key or value using keep or delete decision-makers.
 * <ol>
 *    <li>Deletion of the keys matching the delete key predicate is done first</li>
 *    <li>then the key not matching the keep key predicate will be removed</li>
 *    <li>then the value deletion predicate is used to remove the matching values</li>
 *    <li>then the keep value predicate is used to keep only matching values.</li>
 * </ol>
 */
@Slf4j
public final class JsonDeleteFromMapRule extends JsonMapRuleSupport {

    static final String PARAM_PREDICATE = "predicate";
    static final String RULE_NAME = "deleteFrom";
    static final String PARAM_KEEP_KEY = "keepKey";
    static final String PARAM_DELETE_KEY = "deleteKey";
    static final String PARAM_DELETE_VALUE = "deleteValue";
    static final String PARAM_KEEP_VALUE = "keepValue";

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Predicate<Object>> keepIfKeyMatches;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Predicate<Object>> deleteIfKeyMatches;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Predicate<Object>> deleteIfValueMatches;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<Predicate<Object>> keepIfValueMatches;

    @SuppressWarnings("checkstyle:MagicNumber")
    @SchemaDefinition(
            properties = @PropertyDefinitions(
                    value = {
                            @PropertyDefinition(name = PARAM_PREDICATE, required = false,
                                    type = @TypeDefinition(itemType = Predicate.class, itemTypeParams = StringObjectMap.class),
                                    docs = "Determines whether any deletion should be performed for the matching node"),
                            @PropertyDefinition(name = PARAM_KEEP_KEY,
                                    type = @TypeDefinition(itemType = Predicate.class, itemTypeParams = String.class), required = false,
                                    docs = "The predicate that will be used to test whether a key should be kept, "
                                            + "keeping only the matching keys."),
                            @PropertyDefinition(name = PARAM_DELETE_KEY,
                                    type = @TypeDefinition(itemType = Predicate.class, itemTypeParams = String.class), required = false,
                                    docs = "The predicate that will be used to test whether a key should be deleted, "
                                            + "deleting all the matching keys."),
                            @PropertyDefinition(name = PARAM_KEEP_VALUE,
                                    type = @TypeDefinition(itemType = Predicate.class, itemTypeParams = Object.class), required = false,
                                    docs = "The predicate that will be used to test whether a key should be kept, "
                                            + "keeping only the matching keys."),
                            @PropertyDefinition(name = PARAM_DELETE_VALUE,
                                    type = @TypeDefinition(itemType = Predicate.class, itemTypeParams = Object.class), required = false,
                                    docs = "The predicate that will be used to test whether an entry should be deleted, "
                                            + "deleting all of them with matching values."),
                    },
                    minProperties = 1,
                    maxProperties = 5
            ),
            sinceVersion = WikiConstants.VERSION_1_1_0,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_RULES, section = "Delete from"),
            description = {
                    "This rule performs a Map operation on the JSON Node matching the JSON Path of the rule, therefore the path",
                    "must match Objects/Maps to make it work. All parameters are optional, but it is required to fill at least",
                    "one of them in order to have meaningful rule configuration."
            },
            example = @Example(
                    in = "/examples/json/delete-from-account_in.json",
                    out = "/examples/json/delete-from-account_out.json",
                    yml = "/examples/yml/delete-from.yml"
            )
    )
    @NamedRule(RULE_NAME)
    public JsonDeleteFromMapRule(@NotNull final FunctionRegistry functionRegistry,
                                 @NotNull final JsonMapper jsonMapper,
                                 @NotNull final RawJsonRule jsonRule) {
        super(functionRegistry, jsonMapper, jsonRule, log, PARAM_PREDICATE);
        if (jsonRule.getParams().containsKey(PARAM_KEEP_KEY)) {
            this.keepIfKeyMatches = Optional.of(functionRegistry.lookupPredicate(jsonRule.configParamMap(PARAM_KEEP_KEY)));
        } else {
            this.keepIfKeyMatches = Optional.empty();
        }
        if (jsonRule.getParams().containsKey(PARAM_DELETE_KEY)) {
            this.deleteIfKeyMatches = Optional.of(functionRegistry.lookupPredicate(jsonRule.configParamMap(PARAM_DELETE_KEY)));
        } else {
            this.deleteIfKeyMatches = Optional.empty();
        }
        if (jsonRule.getParams().containsKey(PARAM_KEEP_VALUE)) {
            this.keepIfValueMatches = Optional.of(functionRegistry.lookupPredicate(jsonRule.configParamMap(PARAM_KEEP_VALUE)));
        } else {
            this.keepIfValueMatches = Optional.empty();
        }
        if (jsonRule.getParams().containsKey(PARAM_DELETE_VALUE)) {
            this.deleteIfValueMatches = Optional.of(functionRegistry.lookupPredicate(jsonRule.configParamMap(PARAM_DELETE_VALUE)));
        } else {
            this.deleteIfValueMatches = Optional.empty();
        }
    }

    @Override
    protected Map<String, Object> applyChanges(final Map<String, Object> currentMap) {
        final Map<String, Object> result = new TreeMap<>(currentMap);
        removeIfMatches(result, Map.Entry::getKey, deleteIfKeyMatches);
        removeIfMatches(result, Map.Entry::getKey, keepIfKeyMatches.map(Predicate::negate));
        removeIfMatches(result, Map.Entry::getValue, deleteIfValueMatches);
        removeIfMatches(result, Map.Entry::getValue, keepIfValueMatches.map(Predicate::negate));
        return result;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private <T> void removeIfMatches(final Map<String, Object> result,
                                     final Function<Map.Entry<String, Object>, T> entryConverter,
                                     final Optional<Predicate<Object>> predicate) {
        predicate.ifPresent(shouldRemove -> result.entrySet()
                .removeIf(e -> shouldRemove.test(entryConverter.apply(e))));
    }

    @Override
    public String toString() {
        return "JsonRule['" + RULE_NAME + "']"
                + "\n\t\torder: " + getOrder()
                + "\n\t\tpath: '" + getJsonPath().getPath() + "'"
                + "\n\t\tpredicate: " + predicate()
                + "\n\t\tkeepIfKeyMatches: " + keepIfKeyMatches
                + "\n\t\tdeleteIfKeyMatches: " + deleteIfKeyMatches
                + "\n\t\tkeepIfValueMatches: " + keepIfValueMatches
                + "\n\t\tdeleteIfValueMatches: " + deleteIfValueMatches
                + "\n";
    }

}
