package com.github.nagyesta.yippeekijson.metadata.schema.markdown.impl;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.rule.JsonRule;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentType;
import com.github.nagyesta.yippeekijson.metadata.schema.markdown.DocumentationExporter;
import com.github.nagyesta.yippeekijson.metadata.schema.markdown.MarkdownGenerator;
import com.github.nagyesta.yippeekijson.metadata.schema.parser.ComponentContextMetadataParser;
import lombok.NonNull;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.nagyesta.yippeekijson.core.NamedComponentUtil.findAnnotatedConstructorOfNamedComponent;

public final class DocumentationExporterImpl implements DocumentationExporter {

    private final ComponentContextMetadataParser componentContextMetadataParser;
    private final MarkdownGenerator markdownGeneratorFactory;
    private final Map<ComponentType, Map<String, List<ComponentContext>>> componentRegistry = new EnumMap<>(ComponentType.class);
    private final Map<ComponentType, List<Constructor<?>>> constructorRegistry = new EnumMap<>(ComponentType.class);

    public DocumentationExporterImpl(@NonNull final ComponentContextMetadataParser componentContextMetadataParser,
                                     @NonNull final MarkdownGenerator markdownGeneratorFactory) {
        this.componentContextMetadataParser = componentContextMetadataParser;
        this.markdownGeneratorFactory = markdownGeneratorFactory;
    }

    @Override
    public Map<String, String> exportDocumentation(@NonNull final ComponentType componentType) throws IOException {
        final Map<String, List<ComponentContext>> map = componentRegistry.computeIfAbsent(componentType, type -> {
            Map<String, List<ComponentContext>> contextMap = new TreeMap<>();
            constructorRegistry.get(type).stream()
                    .map(componentContextMetadataParser::parse)
                    .forEach(categorizeByFileNames(contextMap));
            return contextMap;
        });
        Map<String, String> markdowns = new TreeMap<>();
        for (final Map.Entry<String, List<ComponentContext>> entry : map.entrySet()) {
            String filename = entry.getKey();
            List<ComponentContext> components = entry.getValue();
            final String template = IOUtils.resourceToString("/markdown/" + filename, StandardCharsets.UTF_8);
            StringBuilder builder = new StringBuilder(template);
            components.stream()
                    .map(this.markdownGeneratorFactory::renderComponent)
                    .forEach(builder::append);
            markdowns.put(filename, builder.toString());
        }
        return markdowns;
    }

    @NotNull
    private Consumer<ComponentContext> categorizeByFileNames(final Map<String, List<ComponentContext>> contextMap) {
        return context -> {
            Assert.notNull(context.getDocumentation(), "Documentation is mandatory for export.");
            final String fileName = context.getDocumentation().getFileName();
            Assert.notNull(fileName, "File name is mandatory for export.");
            final List<ComponentContext> list = contextMap
                    .computeIfAbsent(fileName, key -> new ArrayList<>());
            list.add(context);
        };
    }

    public void setAutoRegisterRules(
            @NotNull final List<Class<? extends JsonRule>> autoRegisterRules) {
        registerClasses(autoRegisterRules, ComponentType.RULE, NamedRule.class);
    }

    public void setAutoRegisterSuppliers(
            @NotNull final List<Class<? extends Supplier<?>>> autoRegisterSuppliers) {
        registerClasses(autoRegisterSuppliers, ComponentType.SUPPLIER, NamedSupplier.class);
    }

    public void setAutoRegisterFunctions(
            @NotNull final List<Class<? extends Function<?, ?>>> autoRegisterFunctions) {
        registerClasses(autoRegisterFunctions, ComponentType.FUNCTION, NamedFunction.class);
    }

    public void setAutoRegisterPredicates(
            @NotNull final List<Class<? extends Predicate<Object>>> autoRegisterPredicates) {
        registerClasses(autoRegisterPredicates, ComponentType.PREDICATE, NamedPredicate.class);
    }

    private <T> void registerClasses(@NotNull final List<Class<? extends T>> list,
                                     @NotNull final ComponentType type,
                                     @NotNull final Class<? extends Annotation> annotation) {
        constructorRegistry.put(type,
                list.stream()
                        .map(componentClass -> findAnnotatedConstructorOfNamedComponent(componentClass, annotation))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()));
    }
}
