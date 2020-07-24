package com.github.nagyesta.yippeekijson.metadata.schema.markdown.impl;

import com.github.nagyesta.yippeekijson.metadata.schema.markdown.MarkdownGenerator;
import com.github.nagyesta.yippeekijson.metadata.schema.parser.ComponentContextMetadataParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

class DocumentationExporterImplTest {

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(mock(ComponentContextMetadataParser.class), null))
                .add(Arguments.of(null, mock(MarkdownGenerator.class)))
                .build();
    }

    @Test
    void testExportDocumentationShouldFailForNull() {

        //given
        final ComponentContextMetadataParser componentContextMetadataParser = mock(ComponentContextMetadataParser.class);
        final MarkdownGenerator markdownFactory = mock(MarkdownGenerator.class);
        DocumentationExporterImpl underTest = new DocumentationExporterImpl(componentContextMetadataParser, markdownFactory);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.exportDocumentation(null));
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldNotAllowNulls(final ComponentContextMetadataParser componentContextMetadataParser,
                                            final MarkdownGenerator markdownFactory) {

        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new DocumentationExporterImpl(componentContextMetadataParser, markdownFactory));
    }
}
