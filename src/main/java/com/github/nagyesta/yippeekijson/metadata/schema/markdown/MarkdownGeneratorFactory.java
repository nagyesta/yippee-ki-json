package com.github.nagyesta.yippeekijson.metadata.schema.markdown;

import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;

public class MarkdownGeneratorFactory implements MarkdownGenerator {

    private final Set<MarkdownGenerator> generators;

    public MarkdownGeneratorFactory(@NotNull final MarkdownGenerator... generators) {
        this.generators = Set.copyOf(Arrays.asList(generators));
    }

    @Override
    public @NotNull String renderComponent(@NotNull final ComponentContext componentContext) {
        return generators.stream()
                .filter(g -> g.supports(componentContext))
                .findFirst()
                .map(g -> g.renderComponent(componentContext))
                .orElseThrow(() -> {
                    throw new IllegalStateException("No suitable generators are registered for: " + componentContext.getComponentType());
                });
    }

    @Override
    public boolean supports(@NotNull final ComponentContext componentContext) {
        return generators.stream()
                .anyMatch(g -> g.supports(componentContext));
    }
}
