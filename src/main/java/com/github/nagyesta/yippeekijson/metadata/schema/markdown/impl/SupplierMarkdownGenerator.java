package com.github.nagyesta.yippeekijson.metadata.schema.markdown.impl;

import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentType;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.DocumentationContext;
import com.github.nagyesta.yippeekijson.metadata.schema.markdown.MarkdownGenerator;
import net.steppschuh.markdowngenerator.MarkdownBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Markdown generator for Supplier implementations.
 */
public class SupplierMarkdownGenerator extends BaseMarkdownGenerator implements MarkdownGenerator {

    @Override
    protected void appendHeader(@NotNull final MarkdownBuilder<?, ?> builder,
                                @NotNull final ComponentContext componentContext,
                                @NotNull final Optional<DocumentationContext> documentation) {
        super.appendHeader(builder, componentContext, documentation);
        final String outputTypeName = resolvedTypeNameOrElseMissing(documentation, DocumentationContext::getOutputType);
        builder.bold(SECTION_OUTPUT_TYPE).append(SPACE).append(outputTypeName).newParagraph();
    }

    @Override
    public boolean supports(@NotNull final ComponentContext componentContext) {
        return componentContext.getComponentType() == ComponentType.SUPPLIER;
    }
}
