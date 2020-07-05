package com.github.nagyesta.yippeekijson.metadata.schema.markdown.impl;

import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.DocumentationContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.PropertyContext;
import com.github.nagyesta.yippeekijson.metadata.schema.markdown.MarkdownGenerator;
import net.steppschuh.markdowngenerator.MarkdownBuilder;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.TextBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.steppschuh.markdowngenerator.Markdown.code;
import static net.steppschuh.markdowngenerator.Markdown.link;

/**
 * Generic superclass of {@link MarkdownGenerator} implementations.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class BaseMarkdownGenerator implements MarkdownGenerator {
    /**
     * Missing documentation text message.
     */
    public static final String MISSING_DOCUMENTATION = "< Missing documentation >";
    /**
     * Indicator for empty parameter list.
     */
    public static final String NO_PARAMETERS = "< NONE >";
    /**
     * Section name for component name.
     */
    public static final String SECTION_NAME = "Name:";
    /**
     * Section name for component class.
     */
    public static final String SECTION_CLASS = "Class:";
    /**
     * Section name for component introduction version.
     */
    public static final String SECTION_SINCE_VERSION = "Since version:";
    /**
     * Section name for parameters.
     */
    public static final String SECTION_PARAMETERS = "Parameters";
    /**
     * Section name for component description.
     */
    public static final String SECTION_DESCRIPTION = "Description";
    /**
     * Section name for example configuration.
     */
    public static final String SECTION_EXAMPLE_CONFIGURATION = "Example configuration";
    /**
     * Section name for output type.
     */
    public static final String SECTION_OUTPUT_TYPE = "Output type:";
    /**
     * Section name for input type.
     */
    public static final String SECTION_INPUT_TYPE = "Input type:";
    /**
     * A single space.
     */
    protected static final String SPACE = " ";
    /**
     * Name column header.
     */
    protected static final String NAME = "Name";
    /**
     * Type column header.
     */
    protected static final String TYPE = "Type";
    /**
     * Description column header.
     */
    protected static final String DESCRIPTION = "Description";
    /**
     * Section header for yml snippets.
     */
    protected static final String APPLYING_THIS_CONFIGURATION = "Applying this configuration";
    /**
     * Section header for input files.
     */
    protected static final String ON_THIS_INPUT = "On this input";
    /**
     * Section header for output files.
     */
    protected static final String PRODUCES_THIS_OUTPUT = "Produces this output";
    /**
     * Section name for empty parameter list.
     */
    protected static final String SECTION_PARAMETERS_EMPTY = "Parameters:";
    private static final String TYPE_PARAM_OPEN = " < ";
    private static final String TYPE_PARAM_CLOSE = " > ";
    private static final String UNKNOWN_TYPE_PARAM = "?";
    private static final String TYPE_PARAM_SEPARATOR = " , ";
    private static final String ROOT = "/";
    private static final String OWN_BASE_PACKAGE = "com.github.nagyesta.yippeekijson";
    private static final String JAVA_EXTENSION = ".java";
    private static final String HTML_EXTENSION = ".html";
    private static final char PACKAGE_SEPARATOR = '.';
    private static final char URI_PATH_SEPARATOR = '/';
    private static final int SECTION_HEADER_LEVEL = 3;

    @NotNull
    @Override
    public String renderComponent(@NotNull final ComponentContext componentContext) {
        Assert.isTrue(supports(componentContext), "Component context is not supported with type: "
                + componentContext.getComponentType());
        MarkdownBuilder<?, ?> builder = new TextBuilder();
        final Optional<DocumentationContext> documentation = Optional.ofNullable(componentContext.getDocumentation());
        appendHeader(builder, componentContext, documentation);
        appendParameters(builder, componentContext);
        documentation.ifPresentOrElse(context -> {
            appendDescription(builder, context);
            appendExample(builder, context);
        }, () -> builder.append(MISSING_DOCUMENTATION).newLine());

        return builder.toString();
    }

    /**
     * Appends the component header to the markdown builder.
     *
     * @param builder          The markdown builder
     * @param componentContext The component context
     * @param documentation    The optional documentation section
     */
    protected void appendHeader(@NotNull final MarkdownBuilder<?, ?> builder,
                                @NotNull final ComponentContext componentContext,
                                @NotNull final Optional<DocumentationContext> documentation) {
        builder.heading(sectionTitleOrElseComponentName(componentContext, documentation), SECTION_HEADER_LEVEL)
                .bold(SECTION_NAME).append(SPACE).code(componentContext.getComponentName()).newParagraph()
                .bold(SECTION_CLASS).append(SPACE).append(javaType(componentContext.getJavaType())).newParagraph()
                .bold(SECTION_SINCE_VERSION).append(SPACE).append(sinceVersionOrElseMissing(documentation)).newParagraph();
    }

    /**
     * Appends the component parameters to the markdown builder.
     *
     * @param builder          The markdown builder
     * @param componentContext The component context
     */
    protected void appendParameters(@NotNull final MarkdownBuilder<?, ?> builder,
                                    @NotNull final ComponentContext componentContext) {
        if (CollectionUtils.isEmpty(componentContext.getProperties())) {
            builder.bold(SECTION_PARAMETERS_EMPTY).append(SPACE).italic(NO_PARAMETERS).newParagraph();
        } else {
            final Table.Builder table = new Table.Builder();
            table.withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                    .addRow(NAME, TYPE, DESCRIPTION);
            for (final Map.Entry<String, PropertyContext> entry : componentContext.getProperties().entrySet()) {
                PropertyContext prop = entry.getValue();
                final PropertyContext leafProp = leafProp(prop);
                final Type propertyType = leafProp.getType();
                table.addRow(code(entry.getKey()),
                        javaType(propertyType),
                        optionalSticker(leafProp) + leafProp.getDocs().orElse(MISSING_DOCUMENTATION));
            }
            builder.bold(SECTION_PARAMETERS).newParagraph()
                    .append(table.build()).newParagraph();
        }
    }

    @NotNull
    private String optionalSticker(final PropertyContext property) {
        if (property.isRequired()) {
            return "";
        }
        return " ![optoinal](https://img.shields.io/badge/optional-blue) ";
    }

    @NotNull
    private PropertyContext leafProp(@NotNull final PropertyContext prop) {
        if (prop.getChild().isPresent()) {
            return leafProp(prop.getChild().get());
        }
        return prop;
    }

    /**
     * Appends the component description to the markdown builder.
     *
     * @param builder       The markdown builder
     * @param documentation The documentation section
     */
    protected void appendDescription(@NotNull final MarkdownBuilder<?, ?> builder,
                                     @NotNull final DocumentationContext documentation) {
        builder.bold(SECTION_DESCRIPTION).newParagraph();
        documentation.getDescription().forEach(value -> builder.append(value).newLine());
        builder.newLine();
    }

    /**
     * Appends the component examples to the markdown builder.
     *
     * @param builder       The markdown builder
     * @param documentation The documentation section
     */
    protected void appendExample(@NotNull final MarkdownBuilder<?, ?> builder,
                                 @NotNull final DocumentationContext documentation) {
        if (documentation.getExampleConfig().isPresent()) {
            builder.newLine()
                    .bold(SECTION_EXAMPLE_CONFIGURATION).newParagraph();
        }
        appendResource(builder, documentation.getExampleConfig(), Language.YAML, APPLYING_THIS_CONFIGURATION);
        appendResource(builder, documentation.getExampleInput(), Language.JSON, ON_THIS_INPUT);
        appendResource(builder, documentation.getExampleOutput(), Language.JSON, PRODUCES_THIS_OUTPUT);
        if (!CollectionUtils.isEmpty(documentation.getExampleNote())) {
            documentation.getExampleNote().forEach(line -> builder.append(line).newLine());
            builder.newLine();
        }
    }

    /**
     * Returns the component section title or the component name depending on availability.
     *
     * @param componentContext The component context
     * @param documentation    The optional documentation section
     * @return title
     */
    @NotNull
    protected String sectionTitleOrElseComponentName(@NotNull final ComponentContext componentContext,
                                                     @NotNull final Optional<DocumentationContext> documentation) {
        return documentation
                .map(DocumentationContext::getSectionTitle)
                .orElse(componentContext.getComponentName());
    }

    /**
     * Returns the component introduction version or a missing documentation note if not available.
     *
     * @param documentation The optional documentation section
     * @return version
     */
    @NotNull
    protected String sinceVersionOrElseMissing(@NotNull final Optional<DocumentationContext> documentation) {
        return documentation
                .map(DocumentationContext::getSinceVersion)
                .orElse(MISSING_DOCUMENTATION);
    }

    /**
     * Returns the resolved type name or a missing indicator.
     *
     * @param documentation The optional documentation section
     * @param typeFunction  The function converting the Java type to a rendered link.
     * @return type
     */
    @NotNull
    protected String resolvedTypeNameOrElseMissing(@NotNull final Optional<DocumentationContext> documentation,
                                                   @NotNull final Function<DocumentationContext, Optional<? extends Type>> typeFunction) {
        return documentation.flatMap(typeFunction)
                .map(this::javaType)
                .orElse(MISSING_DOCUMENTATION);
    }

    /**
     * Converts a Java type into a rendered link.
     *
     * @param javaType The type.
     * @return link
     */
    @NotNull
    protected String javaType(@NotNull final Type javaType) {
        if (javaType instanceof Class) {
            return linkOf((Class<?>) javaType);
        } else if (javaType instanceof ParameterizedType) {
            return parametrizedType((ParameterizedType) javaType);
        } else {
            return code(javaType.getTypeName()).toString();
        }
    }

    @NotNull
    private String parametrizedType(@NotNull final ParameterizedType javaType) {
        StringBuilder builder = new StringBuilder()
                .append(javaType(javaType.getRawType()))
                .append(TYPE_PARAM_OPEN);
        if (ArrayUtils.isEmpty(javaType.getActualTypeArguments())) {
            builder.append(code(UNKNOWN_TYPE_PARAM));
        } else {
            builder.append(Arrays.stream(javaType.getActualTypeArguments())
                    .map(this::javaType)
                    .collect(Collectors.joining(TYPE_PARAM_SEPARATOR)));
        }
        builder.append(TYPE_PARAM_CLOSE);
        return builder.toString();
    }

    /**
     * Appends the contents of an optional resource if present.
     *
     * @param builder  The markdown builder we want to append to
     * @param resource The optional resource
     * @param language The language selector we will use for the code block
     * @param header   The header of the section
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected void appendResource(@NotNull final MarkdownBuilder<?, ?> builder,
                                  @NotNull final Optional<ClassPathResource> resource,
                                  @NotNull final Language language,
                                  @NotNull final String header) {
        if (resource.isEmpty()) {
            return;
        }
        try {
            builder.append(header).newParagraph()
                    .beginCodeBlock(language.name().toLowerCase())
                    .append(IOUtils.resourceToString(ROOT + resource.get().getPath(), StandardCharsets.UTF_8))
                    .end().newParagraph();
        } catch (final IOException e) {
            throw new IllegalStateException("Resource cannot be read: /" + resource.get().getPath(), e);
        }
    }

    @NotNull
    private String linkOf(@NotNull final Class<?> javaClass) {
        if (StringUtils.startsWith(javaClass.getPackageName(), OWN_BASE_PACKAGE)) {
            return link(code(javaClass.getSimpleName()).toString(), ownSourceUrl(javaClass)).toString();
        } else if (StringUtils.startsWith(javaClass.getPackageName(), "java")) {
            return link(code(javaClass.getSimpleName()).toString(), javaApiDocUrl(javaClass)).toString();
        } else {
            return code(javaClass.getSimpleName()).toString();
        }
    }

    @NotNull
    private String ownSourceUrl(@NotNull final Class<?> javaType) {
        return WikiConstants.SOURCE_ROOT + packageToUriPath(javaType) + JAVA_EXTENSION;
    }

    @NotNull
    private String javaApiDocUrl(@NotNull final Class<?> javaType) {
        return WikiConstants.JAVA_BASE_API_ROOT + packageToUriPath(javaType) + HTML_EXTENSION;
    }

    @NotNull
    private String packageToUriPath(@NotNull final Class<?> javaType) {
        return StringUtils.replaceChars(javaType.getName(), PACKAGE_SEPARATOR, URI_PATH_SEPARATOR);
    }
}
