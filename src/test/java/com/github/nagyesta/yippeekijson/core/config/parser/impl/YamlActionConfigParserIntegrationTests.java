package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.config.entities.JsonActions;
import com.github.nagyesta.yippeekijson.core.config.parser.ActionConfigParser;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.InputStream;

@SpringBootTest
@TestPropertySource(properties = "logging.level.root=DEBUG")
class YamlActionConfigParserIntegrationTests {

    private static final String EXAMPLE_YML = "/yaml/example.yml";
    private static final String INVALID_YML = "/yaml/invalid.yml";
    @Autowired
    private ActionConfigParser actionConfigParser;

    @Test
    void testParseStreamShouldWorkForValidYml() throws ConfigParseException {
        //given
        final InputStream stream = this.getClass().getResourceAsStream(EXAMPLE_YML);

        //when
        final JsonActions parse = actionConfigParser.parse(stream);

        //then
        assertExampleParsedWell(parse);
    }

    @Test
    void testParseFileShouldWorkForValidYml() throws ConfigParseException {
        //given
        final File file = new File(this.getClass().getResource(EXAMPLE_YML).getFile());

        //when
        final JsonActions parse = actionConfigParser.parse(file);

        //then
        assertExampleParsedWell(parse);
    }

    @Test
    void testParseStreamShouldFailForInvalidYml() {
        //given
        final InputStream stream = this.getClass().getResourceAsStream(INVALID_YML);

        //when + then exception
        Assertions.assertThrows(ConfigParseException.class, () -> actionConfigParser.parse(stream));
    }

    @Test
    void testParseFileShouldFailForInvalidYml() {
        //given
        final File file = new File(this.getClass().getResource(INVALID_YML).getFile());

        //when + then exception
        Assertions.assertThrows(ConfigParseException.class, () -> actionConfigParser.parse(file));
    }

    @Test
    void testParseFileShouldFailForMissingFile() {
        //given
        final File file = new File(INVALID_YML);

        //when + then exception
        Assertions.assertThrows(ConfigParseException.class, () -> actionConfigParser.parse(file));
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private void assertExampleParsedWell(final JsonActions parse) {
        Assertions.assertNotNull(parse);
        Assertions.assertEquals(2, parse.getActions().size());
        Assertions.assertEquals(3, parse.getActions().get("filter").getRules().size());
        Assertions.assertEquals(6, parse.getActions().get("split-name").getRules().size());
    }

}
