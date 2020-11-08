package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import com.github.nagyesta.yippeekijson.core.exception.ConfigValidationException;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaExporter;
import org.apache.commons.io.FileUtils;
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
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

@LaunchAbortArmed
@SpringBootTest
class YamlSchemaExportControllerIntegrationTest {

    private static final String OUT = "out";
    private static File tempFile;
    private static File tempDirectory;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private YamlSchemaExportController underTest;
    @Autowired
    private JsonSchemaExporter exporter;

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
                .add(Arguments.of(RunConfig.builder().exportMarkdown(true).exportYmlSchema(true)
                        .output(tempFile.getAbsolutePath()).build()))
                .add(Arguments.of(RunConfig.builder().exportMarkdown(true).exportYmlSchema(true)
                        .build()))
                .add(Arguments.of(RunConfig.builder().exportYmlSchema(true)
                        .output(tempFile.getAbsolutePath()).outputDirectory(tempDirectory.getAbsolutePath()).build()))
                .add(Arguments.of(RunConfig.builder().exportYmlSchema(true)
                        .output(tempDirectory.getAbsolutePath()).build()))
                .build();
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("invalidProvider")
    void testProcessShouldThrowExceptionWhenCalledWithInvalidConfig(final RunConfig runConfig) {
        //given

        //when + then exception
        Assertions.assertThrows(ConfigValidationException.class, () -> underTest.process(runConfig));
    }


    @Test
    void testProcessShouldExportFilesWhenCalledWithValidConfig()
            throws ConfigValidationException, ConfigParseException, IOException {
        //given
        final RunConfig runConfig = RunConfig.builder().exportYmlSchema(true).output(tempFile.getAbsolutePath()).build();
        final String expected = exporter.exportSchema();
        Assertions.assertTrue(tempFile.exists());
        final String initial = FileUtils.readFileToString(tempFile, StandardCharsets.UTF_8);
        Assertions.assertNotEquals(expected, initial);

        //when
        underTest.process(runConfig);

        //then
        Assertions.assertTrue(tempFile.exists());
        final String actual = FileUtils.readFileToString(tempFile, StandardCharsets.UTF_8);
        Assertions.assertEquals(expected, actual);
    }
}
