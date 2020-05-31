package com.github.nagyesta.yippeekijson.core.config.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import java.io.File;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FileValidatorTest {

    private static final String PROPERTY = "property";

    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                //no checks on null
                .add(Arguments.of(new FileValidator(PROPERTY, null, null, null, null),
                        mock(File.class),
                        true))
                //exists only
                .add(Arguments.of(new FileValidator(PROPERTY, true, null, null, null),
                        fileMock(true, false, false, true),
                        true))
                .add(Arguments.of(new FileValidator(PROPERTY, false, null, null, null),
                        fileMock(true, false, false, true),
                        false))
                //can read only
                .add(Arguments.of(new FileValidator(PROPERTY, null, true, null, null),
                        fileMock(true, true, false, true),
                        true))
                .add(Arguments.of(new FileValidator(PROPERTY, null, false, null, null),
                        fileMock(true, true, false, true),
                        false))
                //can write only
                .add(Arguments.of(new FileValidator(PROPERTY, null, null, true, null),
                        fileMock(true, true, true, true),
                        true))
                .add(Arguments.of(new FileValidator(PROPERTY, null, null, false, null),
                        fileMock(true, true, true, true),
                        false))
                //is directory only (notice the negation)
                .add(Arguments.of(new FileValidator(PROPERTY, null, null, null, true),
                        fileMock(true, true, true, false),
                        true))
                .add(Arguments.of(new FileValidator(PROPERTY, null, null, null, false),
                        fileMock(true, true, true, false),
                        false))
                .build();
    }

    private static File fileMock(final boolean exists, final boolean canRead, final boolean canWrite, final boolean isFile) {
        final File mock = mock(File.class);
        when(mock.exists()).thenReturn(exists);
        when(mock.canRead()).thenReturn(canRead);
        when(mock.canWrite()).thenReturn(canWrite);
        when(mock.isFile()).thenReturn(isFile);
        when(mock.isDirectory()).thenReturn(!isFile);
        return mock;
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void isValid(final FileValidator underTest, final File input, final boolean expected) {
        //given
        final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        final ConstraintViolationBuilder builder = mock(ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(mock(NodeBuilderCustomizableContext.class));

        //when
        final boolean actual = underTest.isValid(input, context);

        //then
        Assertions.assertEquals(expected, actual);
        if (!expected) {
            verify(context, atLeastOnce()).disableDefaultConstraintViolation();
            verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(anyString());
            verify(builder, atLeastOnce()).addPropertyNode(PROPERTY);
            verify(builder, never()).addConstraintViolation();
        }
    }
}
