package com.github.nagyesta.yippeekijson.core.config.validation;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import com.github.nagyesta.yippeekijson.core.config.validation.YippeeConfigValidator.FailureReasonCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ValidYippeeConfig(messages = {
        @MessageCode(reason = FailureReasonCode.FILE_CAN_BE_READ, message = "Message")
})
@LaunchAbortArmed
class YippeeConfigValidatorTest {

    private static final String CONFIG = "config";
    private static final String INPUT = "input";
    private static final String INCLUDES = "includes";
    private static final String OUTPUT = "output";
    private static final String OUTPUT_DIR = "outputDirectory";

    private static File fileMock(final boolean exists, final boolean canRead, final boolean canWrite, final boolean isFile) {
        final File mock = mock(File.class);
        when(mock.exists()).thenReturn(exists);
        when(mock.canRead()).thenReturn(canRead);
        when(mock.canWrite()).thenReturn(canWrite);
        when(mock.isFile()).thenReturn(isFile);
        when(mock.isDirectory()).thenReturn(!isFile);
        return mock;
    }

    private static Object[][] configFileProvider() {
        return new Object[][]{
                {null, false, false, false, false},
                {CONFIG, false, false, false, false},
                {CONFIG, true, false, false, false},
                {CONFIG, true, true, false, false},
                {CONFIG, true, true, true, true}
        };
    }

    private static Object[][] inputFileProvider() {
        return new Object[][]{
                {null, false, false, false, false},
                {INPUT, false, false, false, false},
                {INPUT, true, false, false, false},
                {INPUT, true, true, false, true},
                {INPUT, true, true, true, true}
        };
    }

    private static Object[][] outputFileProvider() {
        return new Object[][]{
                {null, null,         /*ACL*/ false, false, false, /*In*/ false, false, /*Exp*/ false},
                {OUTPUT, OUTPUT_DIR, /*ACL*/ false, false, false, /*In*/ false, false, /*Exp*/ false},
                {OUTPUT, null,       /*ACL*/ true, true, true,    /*In*/ false, false, /*Exp*/ false},
                {OUTPUT, null,       /*ACL*/ true, true, true,    /*In*/ true, false,  /*Exp*/ true},
                {OUTPUT, null,       /*ACL*/ true, true, false,   /*In*/ true, false,  /*Exp*/ false},
                {OUTPUT, null,       /*ACL*/ true, false, true,   /*In*/ true, false,  /*Exp*/ false},
                {OUTPUT, null,       /*ACL*/ true, false, false,  /*In*/ true, false,  /*Exp*/ false},
                {OUTPUT, null,       /*ACL*/ false, false, false, /*In*/ true, false,  /*Exp*/ true},
                {OUTPUT, null,       /*ACL*/ false, false, false, /*In*/ false, false, /*Exp*/ false},
                {OUTPUT, null,       /*ACL*/ true, true, true,    /*In*/ false, true,  /*Exp*/ true},
                {null, OUTPUT_DIR,   /*ACL*/ false, false, false, /*In*/ false, false, /*Exp*/ true},
                {null, OUTPUT_DIR,   /*ACL*/ true, false, true,   /*In*/ false, false, /*Exp*/ false},
                {null, OUTPUT_DIR,   /*ACL*/ true, true, true,    /*In*/ false, false, /*Exp*/ false},
                {null, OUTPUT_DIR,   /*ACL*/ true, true, false,   /*In*/ false, false, /*Exp*/ true},
                {null, OUTPUT_DIR,   /*ACL*/ true, false, false,  /*In*/ false, false, /*Exp*/ false}
        };
    }

    private static Stream<Arguments> includeProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, true))
                .add(Arguments.of(Collections.emptyList(), true))
                .add(Arguments.of(Collections.singletonList(null), false))
                .add(Arguments.of(Collections.singletonList(INCLUDES), true))
                .build();
    }

    private static Object[][] nullConstructorParamProvider() {
        return new Object[][]{
                {null, null, null, null},
                {mock(FileValidator.class), null, null, null},
                {null, mock(FileValidator.class), null, null},
                {null, null, mock(FileValidator.class), null},
                {null, null, null, mock(FileValidator.class)},
                {mock(FileValidator.class), mock(FileValidator.class), null, null},
                {null, null, mock(FileValidator.class), mock(FileValidator.class)},
                {mock(FileValidator.class), mock(FileValidator.class), mock(FileValidator.class), null},
                {mock(FileValidator.class), mock(FileValidator.class), null, mock(FileValidator.class)},
                {mock(FileValidator.class), null, mock(FileValidator.class), mock(FileValidator.class)},
                {null, mock(FileValidator.class), mock(FileValidator.class), mock(FileValidator.class)}
        };
    }

    private static Stream<Arguments> missingAnnotationConfigProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of((Object) null))
                .add(Arguments.of(YippeeConfigValidatorTest.class.getAnnotation(ValidYippeeConfig.class)))
                .build();
    }

    private static Object[][] nullIsValidProvider() {
        return new Object[][] {
                {null, null},
                {new RunConfig(), null},
                {null, mock(ConstraintValidatorContext.class)}
        };
    }

    @ParameterizedTest
    @MethodSource("nullConstructorParamProvider")
    void testConstructorShouldThrowExceptionWhenCalledWithNulls(final FileValidator configValidator,
                                                                final FileValidator inputValidator,
                                                                final FileValidator outputValidator,
                                                                final FileValidator outputDirValidator) {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new YippeeConfigValidator(configValidator, inputValidator, outputValidator, outputDirValidator));
    }

    @ParameterizedTest
    @MethodSource("missingAnnotationConfigProvider")
    void testInitializeShouldThrowExceptionWhenCalledWithMissingMessages(final ValidYippeeConfig validYippeeConfig) {
        //given
        final YippeeConfigValidator underTest = new YippeeConfigValidator(mock(FileValidator.class),
                mock(FileValidator.class), mock(FileValidator.class), mock(FileValidator.class));

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.initialize(validYippeeConfig));
    }

    @ParameterizedTest
    @MethodSource("nullIsValidProvider")
    void testIsValidShouldThrowExceptionWhenCalledWithNulls(final RunConfig config, final ConstraintValidatorContext context) {
        //given
        final YippeeConfigValidator underTest = new YippeeConfigValidator(mock(FileValidator.class),
                mock(FileValidator.class), mock(FileValidator.class), mock(FileValidator.class));

        underTest.initialize(RunConfig.class.getAnnotation(ValidYippeeConfig.class));

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.isValid(config, context));
    }

    @ParameterizedTest
    @MethodSource("nullIsValidProvider")
    void testVerifyMethodsShouldThrowExceptionWhenCalledWithNulls(final RunConfig config, final ConstraintValidatorContext context) {
        //given
        final YippeeConfigValidator underTest = new YippeeConfigValidator(mock(FileValidator.class),
                mock(FileValidator.class), mock(FileValidator.class), mock(FileValidator.class));

        underTest.initialize(RunConfig.class.getAnnotation(ValidYippeeConfig.class));

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.verifyConfig(config, context));
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.verifyIncludes(config, context));
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.verifyInput(config, context));
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.verifyOutputs(config, context));
    }

    @Test
    void testCallingIsValidShouldThrowExceptionWhenInitializeNotCalledBefore() {
        //given
        final YippeeConfigValidator underTest = new YippeeConfigValidator(mock(FileValidator.class),
                mock(FileValidator.class), mock(FileValidator.class), mock(FileValidator.class));

        //when + then exception
        Assertions.assertThrows(IllegalStateException.class, () ->
                underTest.isValid(new RunConfig(), mock(ConstraintValidatorContext.class)));
    }

    @ParameterizedTest
    @MethodSource("configFileProvider")
    void testVerifyConfig(final String configFile, final boolean exists, final boolean canRead,
                          final boolean isFile, final boolean expected) {
        //given
        final RunConfig config = spy(RunConfig.builder()
                .config(configFile)
                .build());
        doReturn(fileMock(exists, canRead, true, isFile)).when(config).getConfigAsFile();
        final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        final ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(mock(NodeBuilderCustomizableContext.class));

        FileValidator configValidator = new FileValidator(CONFIG, true, true, null, false);
        FileValidator inputValidator = mock(FileValidator.class);
        FileValidator outputFileValidator = mock(FileValidator.class);
        FileValidator outputDirectoryValidator = mock(FileValidator.class);

        final YippeeConfigValidator underTest = spy(new YippeeConfigValidator(configValidator, inputValidator,
                outputFileValidator, outputDirectoryValidator));
        doReturn(true).when(underTest).verifyIncludes(eq(config), eq(context));
        doReturn(true).when(underTest).verifyOutputs(eq(config), eq(context));
        doReturn(true).when(underTest).verifyInput(eq(config), eq(context));

        underTest.initialize(RunConfig.class.getAnnotation(ValidYippeeConfig.class));

        //when
        final boolean actual = underTest.isValid(config, context);

        //then
        Assertions.assertEquals(expected, actual);
        if (!expected && configFile != null) {
            verify(context, atLeastOnce()).disableDefaultConstraintViolation();
            verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(anyString());
            verify(builder, atLeastOnce()).addPropertyNode(CONFIG);
        }
        verify(underTest).isValid(same(config), same(context));
        verify(underTest).verifyConfig(same(config), same(context));
        verify(underTest).verifyIncludes(same(config), same(context));
        verify(underTest).verifyInput(same(config), same(context));
        verify(underTest).verifyOutputs(same(config), same(context));
        verifyNoMoreInteractions(context, builder);
    }

    @ParameterizedTest
    @MethodSource("inputFileProvider")
    void testVerifyInput(final String inputFile, final boolean exists, final boolean canRead,
                         final boolean isFile, final boolean expected) {
        //given
        final RunConfig config = spy(RunConfig.builder()
                .input(inputFile)
                .build());
        doReturn(fileMock(exists, canRead, true, isFile)).when(config).getInputAsFile();
        final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        final ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(mock(NodeBuilderCustomizableContext.class));

        FileValidator configValidator = mock(FileValidator.class);
        FileValidator inputValidator = new FileValidator(INPUT, true, true, null, null);
        FileValidator outputFileValidator = mock(FileValidator.class);
        FileValidator outputDirectoryValidator = mock(FileValidator.class);

        final YippeeConfigValidator underTest = spy(new YippeeConfigValidator(configValidator, inputValidator,
                outputFileValidator, outputDirectoryValidator));
        doReturn(true).when(underTest).verifyIncludes(eq(config), eq(context));
        doReturn(true).when(underTest).verifyOutputs(eq(config), eq(context));
        doReturn(true).when(underTest).verifyConfig(eq(config), eq(context));

        underTest.initialize(RunConfig.class.getAnnotation(ValidYippeeConfig.class));

        //when
        final boolean actual = underTest.isValid(config, context);

        //then
        Assertions.assertEquals(expected, actual);
        if (!expected && inputFile != null) {
            verify(context, atLeastOnce()).disableDefaultConstraintViolation();
            verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(anyString());
            verify(builder, atLeastOnce()).addPropertyNode(INPUT);
        }
        verify(underTest).isValid(same(config), same(context));
        verify(underTest).verifyConfig(same(config), same(context));
        verify(underTest).verifyIncludes(same(config), same(context));
        verify(underTest).verifyInput(same(config), same(context));
        verify(underTest).verifyOutputs(same(config), same(context));
        verifyNoMoreInteractions(context, builder);
    }

    @ParameterizedTest
    @MethodSource("outputFileProvider")
    @SuppressWarnings({"checkstyle:ParameterNumber", "checkstyle:AvoidInlineconditionals"})
    void testVerifyOutputs(final String outputFile, final String outputDir, final boolean exists, final boolean canWrite,
                           final boolean isFile, final boolean singleInput, final boolean inputMissing, final boolean expected) {
        //given
        final RunConfig config = spy(RunConfig.builder()
                .output(outputFile)
                .outputDirectory(outputDir)
                .build());
        doReturn(fileMock(exists, true, canWrite, isFile)).when(config).getOutputAsFile();
        final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        final ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(mock(NodeBuilderCustomizableContext.class));

        FileValidator configValidator = mock(FileValidator.class);
        FileValidator inputValidator = mock(FileValidator.class);
        FileValidator outputFileValidator = new FileValidator(OUTPUT, null, null, true, false);
        FileValidator outputDirectoryValidator = new FileValidator(OUTPUT_DIR, null, null, true, true);

        final YippeeConfigValidator underTest = spy(new YippeeConfigValidator(configValidator, inputValidator,
                outputFileValidator, outputDirectoryValidator));
        doReturn(inputMissing ? Optional.empty() : Optional.of(fileMock(true, true, true, singleInput)))
                .when(underTest).getOptionalInput(eq(config));
        doReturn(true).when(underTest).verifyIncludes(eq(config), eq(context));
        doReturn(true).when(underTest).verifyConfig(eq(config), eq(context));
        doReturn(true).when(underTest).verifyInput(eq(config), eq(context));

        underTest.initialize(RunConfig.class.getAnnotation(ValidYippeeConfig.class));

        //when
        final boolean actual = underTest.isValid(config, context);

        //then
        Assertions.assertEquals(expected, actual);
        if (!expected) {
            verify(context, atLeastOnce()).disableDefaultConstraintViolation();
            verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(anyString());
            if ((outputFile != null && outputDir != null) || (outputFile == null && outputDir == null)) {
                verify(builder, atMostOnce()).addPropertyNode(anyString());
                verify(builder, atLeastOnce()).addConstraintViolation();
            } else if (outputFile != null) {
                verify(builder, atLeastOnce()).addPropertyNode(OUTPUT);
                verify(builder, never()).addConstraintViolation();
            } else {
                verify(builder, atLeastOnce()).addPropertyNode(OUTPUT_DIR);
                verify(builder, never()).addConstraintViolation();
            }
        }
        verify(underTest).isValid(same(config), same(context));
        verify(underTest).verifyConfig(same(config), same(context));
        verify(underTest).verifyIncludes(same(config), same(context));
        verify(underTest).verifyInput(same(config), same(context));
        verify(underTest).verifyOutputs(same(config), same(context));
        verifyNoMoreInteractions(context, builder);
    }

    @ParameterizedTest
    @MethodSource("includeProvider")
    void testVerifyIncludes(final List<String> includes, final boolean expected) {
        //given
        final RunConfig config = spy(RunConfig.builder()
                .includes(includes)
                .build());
        final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        final ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(mock(NodeBuilderCustomizableContext.class));
        FileValidator configValidator = mock(FileValidator.class);
        FileValidator inputValidator = mock(FileValidator.class);
        FileValidator outputFileValidator = mock(FileValidator.class);
        FileValidator outputDirectoryValidator = mock(FileValidator.class);

        final YippeeConfigValidator underTest = spy(new YippeeConfigValidator(configValidator, inputValidator,
                outputFileValidator, outputDirectoryValidator));
        doReturn(true).when(underTest).verifyOutputs(eq(config), eq(context));
        doReturn(true).when(underTest).verifyConfig(eq(config), eq(context));
        doReturn(true).when(underTest).verifyInput(eq(config), eq(context));

        underTest.initialize(RunConfig.class.getAnnotation(ValidYippeeConfig.class));

        //when
        final boolean actual = underTest.isValid(config, context);

        //then
        Assertions.assertEquals(expected, actual);
        if (!expected) {
            verify(context, atLeastOnce()).disableDefaultConstraintViolation();
            verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(anyString());
            verify(builder, atLeastOnce()).addPropertyNode(INCLUDES);
            verify(builder, never()).addConstraintViolation();
        }
        verify(underTest).isValid(same(config), same(context));
        verify(underTest).verifyConfig(same(config), same(context));
        verify(underTest).verifyIncludes(same(config), same(context));
        verify(underTest).verifyInput(same(config), same(context));
        verify(underTest).verifyOutputs(same(config), same(context));
        verifyNoMoreInteractions(context, builder);
    }
}
