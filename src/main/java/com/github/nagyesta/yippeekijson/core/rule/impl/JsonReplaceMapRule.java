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
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A simple rule to replace {@link Map} valued fields at a {@link com.jayway.jsonpath.JsonPath}, matching a
 * {@link java.util.function.Predicate}.
 */
@Slf4j
public final class JsonReplaceMapRule extends JsonMapRuleSupport {

    static final String PARAM_PREDICATE = "predicate";
    static final String RULE_NAME = "replaceMap";
    static final String PARAM_MAP_FUNCTION = "mapFunction";

    private final Function<Map<String, Object>, Map<String, Object>> mapFunction;

    @SchemaDefinition(
            properties = @PropertyDefinitions({
                    @PropertyDefinition(name = PARAM_PREDICATE, required = false,
                            type = @TypeDefinition(itemType = Predicate.class, itemTypeParams = StringObjectMap.class),
                            docs = "The predicate that will determine whether we need to run the rule on the matching path."),
                    @PropertyDefinition(name = PARAM_MAP_FUNCTION,
                            type = @TypeDefinition(
                                    itemType = Function.class,
                                    itemTypeParams = {StringObjectMap.class, StringObjectMap.class}),
                            docs = "The function that defines the transformation which needs to be done on the selected map/object.")
            }),
            sinceVersion = WikiConstants.VERSION_1_1_0,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_RULES, section = "Replace map"),
            description = {
                    "This rule performs a Map operation on the JSON Node matching the JSON Path of the rule, therefore the path",
                    "must match Objects/Maps to make it work. The optional Predicate can be used to condition the transformation",
                    "on a test, while the Function is responsible for the heavy-lifting by defining the transformation."
            },
            example = @Example(
                    in = "/examples/json/account_replace-map_in.json",
                    out = "/examples/json/account_replace-map_out.json",
                    yml = "/examples/yml/replace-map.yml"
            )
    )
    @NamedRule(RULE_NAME)
    public JsonReplaceMapRule(@NotNull final FunctionRegistry functionRegistry,
                              @NotNull final JsonMapper jsonMapper,
                              @NotNull final RawJsonRule jsonRule) {
        super(functionRegistry, jsonMapper, jsonRule, log, PARAM_PREDICATE);
        this.mapFunction = functionRegistry.lookupFunction(jsonRule.configParamMap(PARAM_MAP_FUNCTION));
    }

    protected Map<String, Object> applyChanges(final Map<String, Object> map) {
        return this.mapFunction.apply(map);
    }

    @Override
    public String toString() {
        return "JsonRule['" + RULE_NAME + "']"
                + "\n\t\torder: " + getOrder()
                + "\n\t\tpath: '" + getJsonPath().getPath() + "'"
                + "\n\t\tpredicate: " + predicate()
                + "\n\t\tfunction: " + mapFunction
                + "\n";
    }

}
