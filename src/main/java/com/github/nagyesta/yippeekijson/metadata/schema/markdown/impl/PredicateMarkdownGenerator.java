package com.github.nagyesta.yippeekijson.metadata.schema.markdown.impl;

import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentType;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.DocumentationContext;
import com.github.nagyesta.yippeekijson.metadata.schema.markdown.MarkdownGenerator;
import net.steppschuh.markdowngenerator.MarkdownBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Markdown generator for Predicate implementations.
 */
public class PredicateMarkdownGenerator extends BaseMarkdownGenerator implements MarkdownGenerator {

    @Override
    protected void appendHeader(@NotNull final MarkdownBuilder<?, ?> builder,
                                @NotNull final ComponentContext componentContext,
                                @NotNull final Optional<DocumentationContext> documentation) {
        super.appendHeader(builder, componentContext, documentation);
        final String inputTypeName = resolvedTypeNameOrElseMissing(documentation, DocumentationContext::getInputType);
        builder.bold(SECTION_INPUT_TYPE).append(SPACE).append(inputTypeName).newParagraph();
    }

    @Override
    public boolean supports(@NotNull final ComponentContext componentContext) {
        return componentContext.getComponentType() == ComponentType.PREDICATE;
    }
}
