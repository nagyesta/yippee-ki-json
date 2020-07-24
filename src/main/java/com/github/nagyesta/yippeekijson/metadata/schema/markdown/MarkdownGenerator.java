package com.github.nagyesta.yippeekijson.metadata.schema.markdown;

import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import org.jetbrains.annotations.NotNull;

/**
 * Defines common behavior of classes rendering {@link ComponentContext} entries as Markdown.
 */
public interface MarkdownGenerator {

    /**
     * Renders a single component into a snippet.
     *
     * @param componentContext The component to render.
     * @return the markdown text
     */
    @NotNull
    String renderComponent(@NotNull ComponentContext componentContext);

    /**
     * Returns true if the given context is supported by this implementation.
     *
     * @param componentContext The context
     * @return true if supported false otherwise
     */
    boolean supports(@NotNull ComponentContext componentContext);
}
