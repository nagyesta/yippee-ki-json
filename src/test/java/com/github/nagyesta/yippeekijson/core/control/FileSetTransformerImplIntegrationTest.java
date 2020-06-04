package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.Map;

class FileSetTransformerImplIntegrationTest {

    private static final String OUT = "out";
    private static final String EXAMPLE_JSON = "example.json";
    private static final String EXAMPLE_FILTERED_JSON = "example-filtered.json";
    private static final String JSON = "/json/";
    private static final String JSON_EXAMPLE_JSON = JSON + EXAMPLE_JSON;
    private static final String JSON_EXAMPLE_FILTERED_JSON = JSON + EXAMPLE_FILTERED_JSON;
    private static final String ANY_JSON = "*.json";
    private static final String ANY_SPLIT_JSON = "*-split.json";

    @Test
    void testTransformToFilePairsShouldWorkForValidFileAsInput() {
        //given
        final File outputFile = new File(new File(OUT), EXAMPLE_JSON);
        final RunConfig runConfig = RunConfig.builder()
                .input(gerFileResource(JSON_EXAMPLE_JSON).getAbsolutePath())
                .output(outputFile.getAbsolutePath())
                .build();

        final FileSetTransformer underTest = new FileSetTransformerImpl();

        //when
        final Map<File, File> actual = underTest.transformToFilePairs(runConfig);

        //then
        Assertions.assertEquals(1, actual.size());
        Assertions.assertTrue(actual.containsKey(gerFileResource(JSON_EXAMPLE_JSON)));
        Assertions.assertEquals(outputFile.getAbsoluteFile(), actual.get(gerFileResource(JSON_EXAMPLE_JSON)));
    }

    @Test
    void testTransformToFilePairsShouldWorkForValidDirectoryAsInput() {
        //given
        final File out = new File(OUT);
        final RunConfig runConfig = RunConfig.builder()
                .input(gerFileResource(JSON_EXAMPLE_JSON).getParent())
                .includes(Collections.singletonList(ANY_JSON))
                .excludes(Collections.singletonList(ANY_SPLIT_JSON))
                .outputDirectory(OUT)
                .build();

        final FileSetTransformer underTest = new FileSetTransformerImpl();

        //when
        final Map<File, File> actual = underTest.transformToFilePairs(runConfig);

        //then
        Assertions.assertEquals(2, actual.size());
        Assertions.assertTrue(actual.containsKey(gerFileResource(JSON_EXAMPLE_JSON)));
        Assertions.assertTrue(actual.containsKey(gerFileResource(JSON_EXAMPLE_FILTERED_JSON)));
        Assertions.assertEquals(new File(out, EXAMPLE_JSON).getAbsoluteFile(),
                actual.get(gerFileResource(JSON_EXAMPLE_JSON)));
        Assertions.assertEquals(new File(out, EXAMPLE_FILTERED_JSON).getAbsoluteFile(),
                actual.get(gerFileResource(JSON_EXAMPLE_FILTERED_JSON)));
    }

    @Test
    void testTransformToFilePairsShouldWorkForValidDirectoryMatchingOneFileInputAndSingleFileOutput() {
        //given
        final File outputFile = new File(OUT);
        final RunConfig runConfig = RunConfig.builder()
                .input(gerFileResource(JSON_EXAMPLE_JSON).getParent())
                .includes(Collections.singletonList(EXAMPLE_JSON))
                .excludes(Collections.emptyList())
                .output(OUT)
                .build();

        final FileSetTransformer underTest = new FileSetTransformerImpl();

        //when
        final Map<File, File> actual = underTest.transformToFilePairs(runConfig);

        //then
        Assertions.assertEquals(1, actual.size());
        Assertions.assertTrue(actual.containsKey(gerFileResource(JSON_EXAMPLE_JSON)));
        Assertions.assertEquals(outputFile.getAbsoluteFile(), actual.get(gerFileResource(JSON_EXAMPLE_JSON)));
    }

    @Test
    void testTransformToFilePairsShouldFailForValidDirectoryMatchingMultipleFilesInputAndSingleFileOutput() {
        //given
        final RunConfig runConfig = RunConfig.builder()
                .input(gerFileResource(JSON_EXAMPLE_JSON).getParent())
                .includes(Collections.singletonList(ANY_JSON))
                .includes(Collections.emptyList())
                .output(OUT)
                .build();

        final FileSetTransformer underTest = new FileSetTransformerImpl();

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.transformToFilePairs(runConfig));
    }

    @Test
    void testTransformToFilePairsShouldNotAllowNull() {
        //given
        final FileSetTransformer underTest = new FileSetTransformerImpl();

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.transformToFilePairs(null));
    }

    private File gerFileResource(final String resource) {
        return new File(this.getClass().getResource(resource).getFile());
    }
}
