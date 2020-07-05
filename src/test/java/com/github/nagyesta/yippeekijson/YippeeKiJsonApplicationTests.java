package com.github.nagyesta.yippeekijson;

import com.github.nagyesta.yippeekijson.core.config.entities.JsonActions;
import com.github.nagyesta.yippeekijson.core.config.entities.RunConfig;
import com.github.nagyesta.yippeekijson.core.config.parser.ActionConfigParser;
import com.github.nagyesta.yippeekijson.core.control.ApplicationController;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import com.github.nagyesta.yippeekijson.core.exception.ConfigValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@SpringBootTest
class YippeeKiJsonApplicationTests {

    private static final String MESSAGE = "message";
    private static final IllegalArgumentException CAUSE = new IllegalArgumentException();
    @Autowired
    private ActionConfigParser actionConfigParser;

    @SuppressWarnings("checkstyle:MagicNumber")
    private static Stream<Arguments> exceptionProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(Optional.empty(), false, 0))
                .add(Arguments.of(Optional.of(new ConfigValidationException(MESSAGE)), false, 1))
                .add(Arguments.of(Optional.of(new ConfigValidationException(MESSAGE)), true, 2))
                .add(Arguments.of(Optional.of(new ConfigParseException(MESSAGE, CAUSE)), false, 3))
                .add(Arguments.of(Optional.of(new RuntimeException(MESSAGE, CAUSE)), false, 4))
                .build();
    }

    private static Object[][] nullProvider() {
        return new Object[][]{
                {null, null},
                {new RunConfig(), null},
                {null, mock(ApplicationController.class)}
        };
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldThrowExceptionWhenCalledWithNulls(final RunConfig runConfig,
                                                                final ApplicationController controller) {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new YippeeKiJsonApplication(runConfig, controller));
    }

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    void testRunShouldReturnTheRightExitCode(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") final Optional<Exception> exception,
                                             final boolean helpFails, final int exitCode) throws Exception {
        //given
        final RunConfig runConfig = new RunConfig();
        final ApplicationController controller = mock(ApplicationController.class);
        if (exception.isPresent()) {
            doThrow(exception.get()).when(controller).process(any(RunConfig.class));
        }
        final YippeeKiJsonApplication underTest = spy(new YippeeKiJsonApplication(runConfig, controller));
        if (helpFails) {
            doThrow(new IOException()).when(underTest).printHelp();
        }

        //when
        final int actual = underTest.run();

        //then
        Assertions.assertEquals(exitCode, actual);
        final InOrder inOrder = inOrder(underTest, controller);
        inOrder.verify(underTest).run();
        inOrder.verify(controller).process(same(runConfig));
        if (exception.isPresent() && exception.get() instanceof ConfigValidationException) {
            inOrder.verify(underTest).printHelp();
        }
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    @SuppressWarnings("checkstyle:MagicNumber")
    void testParseConfigFile() throws ConfigParseException {
        final JsonActions parse = actionConfigParser.parse(this.getClass().getResourceAsStream("/yaml/example.yml"), false);
        Assertions.assertNotNull(parse);
        Assertions.assertEquals(2, parse.getActions().size());
        Assertions.assertEquals(3, parse.getActions().get("filter").getRules().size());
        Assertions.assertEquals(6, parse.getActions().get("split-name").getRules().size());
    }

}
