package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.JsonAction;
import com.github.nagyesta.yippeekijson.core.config.entities.JsonActions;
import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import com.github.nagyesta.yippeekijson.core.config.parser.ActionConfigParser;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import com.github.nagyesta.yippeekijson.core.exception.ConfigValidationException;
import com.github.nagyesta.yippeekijson.core.exception.JsonTransformException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

/**
 * Unit test of {@link FilePairProcessorController}.
 */
@SpringBootTest
class FilePairProcessorControllerImplTest {

    private static final String ACTION = "action";
    private static final String CONFIG = "config";
    private static final String INPUT = "input";
    private static final String OUTPUT = "output";
    private static final String INCLUDES = "includes";
    private static final String EMPTY = "";
    private static final String ACTION_NAME = "action-name";
    private static final String TRANSFORMED = "transformed";

    @Autowired
    private Validator validatorBean;

    private static Stream<Arguments> invalidRunConfigProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null,
                        Collections.emptySet()))
                .add(Arguments.of(RunConfig.builder()
                                .action(null)
                                .allowOverwrite(true)
                                .config(null)
                                .excludes(Collections.emptyList())
                                .includes(Collections.emptyList())
                                .input(null)
                                .output(null)
                                .outputDirectory(null)
                                .build(),
                        Set.of(ACTION, CONFIG, INPUT, INCLUDES, EMPTY)))
                .add(Arguments.of(RunConfig.builder()
                                .action(EMPTY)
                                .allowOverwrite(true)
                                .config(EMPTY)
                                .excludes(Collections.emptyList())
                                .includes(Collections.emptyList())
                                .input(EMPTY)
                                .output(EMPTY)
                                .outputDirectory(null)
                                .build(),
                        Set.of(ACTION, CONFIG, INPUT, INCLUDES, EMPTY)))
                .add(Arguments.of(RunConfig.builder()
                                .action(ACTION)
                                .allowOverwrite(false)
                                .config(CONFIG)
                                .excludes(Collections.emptyList())
                                .includes(Collections.emptyList())
                                .input(INPUT)
                                .output(OUTPUT)
                                .outputDirectory(null)
                                .build(),
                        Set.of(CONFIG, INPUT, INCLUDES)))
                .add(Arguments.of(RunConfig.builder()
                                .action(ACTION)
                                .allowOverwrite(false)
                                .config(CONFIG)
                                .excludes(Collections.emptyList())
                                .includes(Collections.emptyList())
                                .input(INPUT)
                                .output(null)
                                .outputDirectory(OUTPUT)
                                .build(),
                        Set.of(CONFIG, INPUT, INCLUDES)))
                .build();
    }

    @Test
    void testProcessShouldSkipWriteWhenTransformFails() throws ConfigValidationException, ConfigParseException,
            JsonTransformException, IOException {
        //given
        final JsonTransformer jsonTransformer = mock(JsonTransformer.class);
        final FileSetTransformer fileSetTransformer = mock(FileSetTransformer.class);
        final ActionConfigParser configParser = mock(ActionConfigParser.class);
        final Validator validator = mock(Validator.class);
        when(validator.validate(any(RunConfig.class))).thenReturn(Collections.emptySet());

        final RunConfig runConfig = spy(RunConfig.builder()
                .action(ACTION_NAME)
                .config(CONFIG)
                .allowOverwrite(false)
                .input(INPUT)
                .output(OUTPUT)
                .build());

        final File configFileMock = mock(File.class);
        doReturn(configFileMock).when(runConfig).getConfigAsFile();

        final File inputFileMock = mock(File.class);
        doReturn(inputFileMock).when(runConfig).getInputAsFile();

        final File outputFileMock = mock(File.class);
        doReturn(outputFileMock).when(runConfig).getOutputAsFile();
        when(outputFileMock.exists()).thenReturn(false);

        final JsonAction jsonAction = JsonAction.builder().name(ACTION_NAME).build();
        final JsonActions jsonActions = JsonActions.builder().addAction(ACTION_NAME, jsonAction).build();
        when(configParser.parse(any(File.class))).thenReturn(jsonActions);
        when(fileSetTransformer.transformToFilePairs(eq(runConfig))).thenReturn(Map.of(inputFileMock, outputFileMock));
        when(jsonTransformer.transform(any(File.class), any(JsonAction.class)))
                .thenThrow(new JsonTransformException("message", new IllegalArgumentException()));

        final FilePairProcessorControllerImpl underTest = spy(new FilePairProcessorControllerImpl(
                jsonTransformer, fileSetTransformer, configParser, validator));

        //when
        underTest.process(runConfig);

        //then
        final InOrder inOrder = Mockito.inOrder(underTest, jsonTransformer, fileSetTransformer, configParser,
                validator, configFileMock, inputFileMock, outputFileMock);
        inOrder.verify(underTest).process(same(runConfig));
        inOrder.verify(underTest).validateConfig(same(runConfig));
        inOrder.verify(validator).validate(same(runConfig));
        inOrder.verify(configParser).parse(eq(runConfig.getConfigAsFile()));
        inOrder.verify(fileSetTransformer).transformToFilePairs(same(runConfig));
        inOrder.verify(jsonTransformer).transform(eq(runConfig.getInputAsFile()), same(jsonAction));
        inOrder.verify(underTest, never()).writeToFile(any(File.class), anyString());
        inOrder.verify(underTest, times(2)).summarize(anyMap());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testProcessShouldSkipFilesWhenOverwriteNotAllowed() throws ConfigValidationException, ConfigParseException,
            JsonTransformException, IOException {
        //given
        final JsonTransformer jsonTransformer = mock(JsonTransformer.class);
        final FileSetTransformer fileSetTransformer = mock(FileSetTransformer.class);
        final ActionConfigParser configParser = mock(ActionConfigParser.class);
        final Validator validator = mock(Validator.class);
        when(validator.validate(any(RunConfig.class))).thenReturn(Collections.emptySet());

        final RunConfig runConfig = spy(RunConfig.builder()
                .action(ACTION_NAME)
                .config(CONFIG)
                .allowOverwrite(false)
                .input(INPUT)
                .output(OUTPUT)
                .build());

        final File configFileMock = mock(File.class);
        doReturn(configFileMock).when(runConfig).getConfigAsFile();

        final File inputFileMock = mock(File.class);
        doReturn(inputFileMock).when(runConfig).getInputAsFile();

        final File outputFileMock = mock(File.class);
        doReturn(outputFileMock).when(runConfig).getOutputAsFile();
        when(outputFileMock.exists()).thenReturn(true);

        final JsonAction jsonAction = JsonAction.builder().name(ACTION_NAME).build();
        final JsonActions jsonActions = JsonActions.builder().addAction(ACTION_NAME, jsonAction).build();
        when(configParser.parse(any(File.class))).thenReturn(jsonActions);
        when(fileSetTransformer.transformToFilePairs(eq(runConfig))).thenReturn(Map.of(inputFileMock, outputFileMock));

        final FilePairProcessorControllerImpl underTest = spy(new FilePairProcessorControllerImpl(
                jsonTransformer, fileSetTransformer, configParser, validator));

        //when
        underTest.process(runConfig);

        //then
        final InOrder inOrder = Mockito.inOrder(underTest, jsonTransformer, fileSetTransformer, configParser,
                validator, configFileMock, inputFileMock, outputFileMock);
        inOrder.verify(underTest).process(same(runConfig));
        inOrder.verify(underTest).validateConfig(same(runConfig));
        inOrder.verify(validator).validate(same(runConfig));
        inOrder.verify(configParser).parse(eq(runConfig.getConfigAsFile()));
        inOrder.verify(fileSetTransformer).transformToFilePairs(same(runConfig));
        inOrder.verify(jsonTransformer, never()).transform(eq(runConfig.getInputAsFile()), same(jsonAction));
        inOrder.verify(underTest, never()).writeToFile(any(File.class), anyString());
        inOrder.verify(underTest, times(2)).summarize(anyMap());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void testProcessShouldProcessFileWhenValidConfigProvided() throws ConfigValidationException, ConfigParseException,
            JsonTransformException, IOException {
        //given
        final JsonTransformer jsonTransformer = mock(JsonTransformer.class);
        final FileSetTransformer fileSetTransformer = mock(FileSetTransformer.class);
        final ActionConfigParser configParser = mock(ActionConfigParser.class);
        final Validator validator = mock(Validator.class);
        when(validator.validate(any(RunConfig.class))).thenReturn(Collections.emptySet());

        final RunConfig runConfig = spy(RunConfig.builder()
                .action(ACTION_NAME)
                .config(CONFIG)
                .input(INPUT)
                .output(OUTPUT)
                .build());

        final File configFileMock = mock(File.class);
        doReturn(configFileMock).when(runConfig).getConfigAsFile();

        final File inputFileMock = mock(File.class);
        doReturn(inputFileMock).when(runConfig).getInputAsFile();

        final File outputFileMock = mock(File.class);
        doReturn(outputFileMock).when(runConfig).getOutputAsFile();

        final JsonAction jsonAction = JsonAction.builder().name(ACTION_NAME).build();
        final JsonActions jsonActions = JsonActions.builder().addAction(ACTION_NAME, jsonAction).build();
        when(configParser.parse(any(File.class))).thenReturn(jsonActions);
        when(fileSetTransformer.transformToFilePairs(eq(runConfig))).thenReturn(Map.of(inputFileMock, outputFileMock));
        when(jsonTransformer.transform(eq(inputFileMock), eq(jsonAction))).thenReturn(TRANSFORMED);

        final FilePairProcessorControllerImpl underTest = spy(new FilePairProcessorControllerImpl(
                jsonTransformer, fileSetTransformer, configParser, validator));
        doNothing().when(underTest).writeToFile(eq(outputFileMock), eq(TRANSFORMED));

        //when
        underTest.process(runConfig);

        //then
        final InOrder inOrder = Mockito.inOrder(underTest, jsonTransformer, fileSetTransformer, configParser,
                validator, configFileMock, inputFileMock, outputFileMock);
        inOrder.verify(underTest).process(same(runConfig));
        inOrder.verify(underTest).validateConfig(same(runConfig));
        inOrder.verify(validator).validate(same(runConfig));
        inOrder.verify(configParser).parse(eq(runConfig.getConfigAsFile()));
        inOrder.verify(fileSetTransformer).transformToFilePairs(same(runConfig));
        inOrder.verify(jsonTransformer).transform(eq(runConfig.getInputAsFile()), same(jsonAction));
        inOrder.verify(underTest).writeToFile(eq(runConfig.getOutputAsFile()), eq(TRANSFORMED));
        inOrder.verify(underTest, times(2)).summarize(anyMap());
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource("invalidRunConfigProvider")
    void testValidateConfigShouldThrowExceptionOnViolation(final RunConfig config, final Set<String> expectedViolations) {
        //given
        final JsonTransformer jsonTransformer = mock(JsonTransformer.class);
        final FileSetTransformer fileSetTransformer = mock(FileSetTransformer.class);
        final ActionConfigParser configParser = mock(ActionConfigParser.class);

        final FilePairProcessorController underTest = new FilePairProcessorControllerImpl(
                jsonTransformer, fileSetTransformer, configParser, validatorBean);

        //when
        try {
            underTest.validateConfig(config);
            Assertions.fail("Should have thrown an exception.");
        } catch (final ConfigValidationException e) {
            //then exception
            final Set<ConstraintViolation<RunConfig>> violations = e.getViolations();
            Assertions.assertNotNull(violations);
            final Set<String> actual = violations.stream()
                    .map(ConstraintViolation::getPropertyPath)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
            expectedViolations.forEach(v ->
                    Assertions.assertTrue(actual.contains(v), "Missing violation: " + v)
            );
            actual.forEach(v ->
                    Assertions.assertTrue(expectedViolations.contains(v), "Unexpected violation: " + v)
            );
            Assertions.assertEquals(expectedViolations.size(), actual.size());
        }
    }

    @Test
    void testWriteToFileShouldWriteContentToFile() throws IOException {
        //given
        final String content = TRANSFORMED;
        final File file = File.createTempFile("yippee-test-file", ".json");
        file.deleteOnExit();

        final JsonTransformer jsonTransformer = mock(JsonTransformer.class);
        final FileSetTransformer fileSetTransformer = mock(FileSetTransformer.class);
        final ActionConfigParser configParser = mock(ActionConfigParser.class);
        final Validator validator = mock(Validator.class);

        final FilePairProcessorControllerImpl underTest = new FilePairProcessorControllerImpl(
                jsonTransformer, fileSetTransformer, configParser, validator);

        //when
        underTest.writeToFile(file, content);

        //then
        final String actual = IOUtils.toString(file.toURI(), StandardCharsets.UTF_8);
        Assertions.assertEquals(content, actual);
        FileUtils.deleteQuietly(file);
    }
}
