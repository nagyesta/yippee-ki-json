package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import com.github.nagyesta.yippeekijson.core.exception.ConfigValidationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

@SpringBootTest
class MarkdownExportControllerIntegrationTest {

    private static final String OUT = "out";
    private static File tempFile;
    private static File tempDirectory;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MarkdownExportController underTest;

    @BeforeAll
    static void beforeAll() throws IOException {
        tempFile = File.createTempFile(OUT, OUT);
        tempFile.deleteOnExit();
        tempDirectory = new File(FileUtils.getTempDirectory(), "yippeee-test-run" + System.currentTimeMillis());
        Assertions.assertTrue(tempDirectory.mkdir());
    }

    @AfterAll
    static void afterAll() throws IOException {
        FileUtils.deleteDirectory(tempDirectory);
        FileUtils.deleteQuietly(tempFile);
    }

    private static Stream<Arguments> invalidProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(RunConfig.builder().exportMarkdown(true).exportYmlSchema(true).outputDirectory(OUT).build()))
                .add(Arguments.of(RunConfig.builder().exportMarkdown(true).exportYmlSchema(true).build()))
                .add(Arguments.of(RunConfig.builder().exportMarkdown(true).output(OUT).outputDirectory(OUT).build()))
                .add(Arguments.of(RunConfig.builder().exportMarkdown(true).outputDirectory(tempFile.getAbsolutePath()).build()))
                .build();
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("invalidProvider")
    void testProcessShouldThrowExceptionWhenCalledWithInvalidConfig(final RunConfig runConfig) {
        //given

        //when + then exception
        Assertions.assertThrows(ConfigValidationException.class, () -> {
            underTest.process(runConfig);
        });
    }


    @Test
    void testProcessShouldExportFilesWhenCalledWithValidConfig() throws ConfigValidationException, ConfigParseException {
        //given
        final RunConfig runConfig = RunConfig.builder().exportMarkdown(true).outputDirectory(tempDirectory.getAbsolutePath()).build();
        Assertions.assertTrue(tempDirectory.exists());
        Assertions.assertTrue(ArrayUtils.isEmpty(tempDirectory.list()));

        //when
        underTest.process(runConfig);

        //then
        Assertions.assertTrue(tempDirectory.exists());
        final String[] actual = tempDirectory.list();
        Assertions.assertTrue(ArrayUtils.isNotEmpty(actual));
        Assertions.assertTrue(Arrays.stream(actual).allMatch(name -> name.endsWith(".md")));
    }
}
