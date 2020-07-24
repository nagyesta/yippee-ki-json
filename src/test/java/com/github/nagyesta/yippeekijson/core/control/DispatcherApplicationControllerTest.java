package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import com.github.nagyesta.yippeekijson.core.exception.ConfigValidationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

class DispatcherApplicationControllerTest {

    private static Stream<Arguments> validInputProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(RunConfig.builder().exportYmlSchema(true).build(), true, false, false))
                .add(Arguments.of(RunConfig.builder().exportMarkdown(true).build(), false, true, false))
                .add(Arguments.of(RunConfig.builder().build(), false, false, true))
                .add(Arguments.of(null, false, true, false))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testProcessShouldCallTheRightController(
            final RunConfig runConfig, final boolean ymlCalled, final boolean markdownCalled, final boolean transformCalled)
            throws ConfigValidationException, ConfigParseException {
        //given
        ApplicationController yaml = mock(ApplicationController.class);
        ApplicationController markdown = mock(ApplicationController.class);
        ApplicationController filePair = mock(ApplicationController.class);
        ApplicationController underTest = new DispatcherApplicationController(filePair, markdown, yaml);

        //when
        underTest.process(runConfig);

        //then
        if (ymlCalled) {
            verify(yaml).process(same(runConfig));
        } else {
            verifyNoInteractions(yaml);
        }
        if (markdownCalled) {
            verify(markdown).process(same(runConfig));
        } else {
            verifyNoInteractions(markdown);
        }
        if (transformCalled) {
            verify(filePair).process(same(runConfig));
        } else {
            verifyNoInteractions(filePair);
        }
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testValidateConfigShouldCallTheRightController(
            final RunConfig runConfig, final boolean ymlCalled, final boolean markdownCalled, final boolean transformCalled)
            throws ConfigValidationException, ConfigParseException {
        //given
        ApplicationController yaml = mock(ApplicationController.class);
        ApplicationController markdown = mock(ApplicationController.class);
        ApplicationController filePair = mock(ApplicationController.class);
        ApplicationController underTest = new DispatcherApplicationController(filePair, markdown, yaml);

        //when
        underTest.validateConfig(runConfig);

        //then
        if (ymlCalled) {
            verify(yaml).validateConfig(same(runConfig));
        } else {
            verifyNoInteractions(yaml);
        }
        if (markdownCalled) {
            verify(markdown).validateConfig(same(runConfig));
        } else {
            verifyNoInteractions(markdown);
        }
        if (transformCalled) {
            verify(filePair).validateConfig(same(runConfig));
        } else {
            verifyNoInteractions(filePair);
        }
    }
}
