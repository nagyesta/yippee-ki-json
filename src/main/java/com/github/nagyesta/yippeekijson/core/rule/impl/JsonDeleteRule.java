package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.AbstractJsonRule;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a simple rule deleting nodes matching the selected {@link JsonPath}.
 */
@Slf4j
public final class JsonDeleteRule extends AbstractJsonRule {

    static final String RULE_NAME = "delete";

    @SuppressWarnings("DefaultAnnotationParam")
    @SchemaDefinition(
            sinceVersion = WikiConstants.VERSION_1_0_0,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_RULES, section = "Delete"),
            description = {
                    "This rule deletes all nodes from the document matching the path parameter."
            },
            example = @Example(
                    in = "/examples/json/simple-accounts_in.json",
                    out = "/examples/json/simple-accounts_delete-id_out.json",
                    yml = "/examples/yml/delete-id.yml"
            )
    )
    @NamedRule(RULE_NAME)
    public JsonDeleteRule(@NotNull final RawJsonRule jsonRule) {
        super(jsonRule.getOrder(), JsonPath.compile(jsonRule.getPath()));
    }

    @Override
    public void accept(@NotNull final DocumentContext documentContext) {
        log.info(String.format("Deleting object at jsonPath: \"%s\".", getJsonPath().getPath()));
        documentContext.delete(getJsonPath());
    }

    @Override
    public String toString() {
        return "JsonRule['" + RULE_NAME + "']"
                + "\n\t\torder: " + getOrder()
                + "\n\t\tpath: '" + getJsonPath().getPath() + "'\n";
    }

}
