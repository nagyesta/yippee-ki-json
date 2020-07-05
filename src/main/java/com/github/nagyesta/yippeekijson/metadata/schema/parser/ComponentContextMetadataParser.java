package com.github.nagyesta.yippeekijson.metadata.schema.parser;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.ParameterContext;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.PropertyContext;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Parses the available component metadata using the constructor of the component.
 */
public class ComponentContextMetadataParser {

    private final Map<Class<? extends Annotation>, NamedAnnotationMetadataParser<? extends Annotation>> annotationParsers;
    private final SchemaDefinitionMetadataParser schemaDefinitionParser;

    public ComponentContextMetadataParser(@NotNull final NamedAnnotationMetadataParser<NamedRule> namedRuleParser,
                                          @NotNull final NamedAnnotationMetadataParser<NamedPredicate> namedPredicateParser,
                                          @NotNull final NamedAnnotationMetadataParser<NamedFunction> namedFunctionParser,
                                          @NotNull final NamedAnnotationMetadataParser<NamedSupplier> namedSupplierParser,
                                          @NotNull final SchemaDefinitionMetadataParser schemaDefinitionParser) {
        this.schemaDefinitionParser = schemaDefinitionParser;
        this.annotationParsers = Map.of(
                NamedRule.class, namedRuleParser,
                NamedPredicate.class, namedPredicateParser,
                NamedFunction.class, namedFunctionParser,
                NamedSupplier.class, namedSupplierParser
        );
    }

    /**
     * Parses the available metadata based on the constructor of the component.
     *
     * @param constructor the constructor
     * @return the parsed metadata
     * @throws IllegalArgumentException If the parse fails due to invalid input.
     */
    public ComponentContext parse(@NotNull final Constructor<?> constructor) throws IllegalArgumentException {
        ComponentContext.ComponentContextBuilder builder = ComponentContext.builder();
        parseNamedAnnotation(constructor, builder);
        builder.javaType(constructor.getDeclaringClass());
        final Parameter[] parameters = constructor.getParameters();
        //noinspection CodeBlock2Expr
        Arrays.stream(parameters)
                .filter(ParameterContext::supports)
                .map(ParameterContext::forParameter)
                .forEach(context -> {
                    builder.properties(context.getName(), PropertyContext.builder()
                            .docs(Optional.ofNullable(StringUtils.trimToNull(context.getDocs())))
                            .required(!context.isNullable())
                            .type(context.getResolvedType())
                            .name(context.getName())
                            .pattern(context.getPattern())
                            .build());
                });
        if (constructor.isAnnotationPresent(SchemaDefinition.class)) {
            this.schemaDefinitionParser.mergeInto(constructor.getDeclaredAnnotation(SchemaDefinition.class), builder);
        }

        return builder.build();
    }

    private void parseNamedAnnotation(@NotNull final Constructor<?> constructor,
                                      @NotNull final ComponentContext.ComponentContextBuilder builder) {
        annotationParsers.entrySet()
                .stream()
                .filter(e -> constructor.isAnnotationPresent(e.getKey()))
                .map(e -> Map.entry(constructor.getDeclaredAnnotation(e.getKey()), e.getValue()))
                .findFirst()
                .ifPresentOrElse(match -> match.getValue().mergeInto(match.getKey(), builder), () -> {
                    throw new IllegalArgumentException("Constructor is not annotated: " + constructor.getDeclaringClass());
                });
    }
}
