package com.github.nagyesta.yippeekijson.metadata.schema.markdown.impl;

import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentType;
import com.github.nagyesta.yippeekijson.metadata.schema.markdown.MarkdownGenerator;
import org.jetbrains.annotations.NotNull;

/**
 * Markdown generator for JsonRule implementations.
 */
public class JsonRuleMarkdownGenerator extends BaseMarkdownGenerator implements MarkdownGenerator {

    @Override
    public boolean supports(@NotNull final ComponentContext componentContext) {
        return componentContext.getComponentType() == ComponentType.RULE;
    }
}
