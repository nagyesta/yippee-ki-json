package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.JsonAction;
import com.github.nagyesta.yippeekijson.core.config.entities.JsonActions;
import com.github.nagyesta.yippeekijson.core.config.parser.ActionConfigParser;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import com.github.nagyesta.yippeekijson.core.exception.JsonTransformException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SpringBootTest
class JsonTransformerImplIntegrationTest {

    private static final String YAML_EXAMPLE_YML = "/yaml/example.yml";
    private static final String JSON_EXAMPLE_JSON = "/json/example.json";
    private static final String JSON_EXAMPLE_FILTERED_JSON = "/json/example-filtered.json";
    private static final String FILTER = "filter";
    @Autowired
    private ActionConfigParser actionConfigParser;

    @Test
    void testTransformStreamShouldProcessValidInput() throws ConfigParseException, JsonTransformException, IOException {
        //given
        final InputStream yaml = this.getClass().getResourceAsStream(YAML_EXAMPLE_YML);
        final JsonActions jsonActions = actionConfigParser.parse(yaml);

        final JsonAction action = jsonActions.getActions().get(FILTER);
        final InputStream resource = this.getClass().getResourceAsStream(JSON_EXAMPLE_JSON);

        final JsonTransformer underTest = new JsonTransformerImpl();

        //when
        final String actual = underTest.transform(resource, action);

        //then
        final String expected = IOUtils.resourceToString(JSON_EXAMPLE_FILTERED_JSON, StandardCharsets.UTF_8);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testTransformStreamShouldHandleEmptyRuleSet() throws JsonTransformException, IOException {
        //given
        final JsonAction action = JsonAction.builder().name(FILTER).build();
        final InputStream resource = this.getClass().getResourceAsStream(JSON_EXAMPLE_JSON);

        final JsonTransformer underTest = new JsonTransformerImpl();

        //when
        final String actual = underTest.transform(resource, action);

        //then
        final String expected = IOUtils.resourceToString(JSON_EXAMPLE_JSON, StandardCharsets.UTF_8);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testTransformFileShouldProcessValidInput() throws ConfigParseException, JsonTransformException, IOException {
        //given
        final InputStream yaml = this.getClass().getResourceAsStream(YAML_EXAMPLE_YML);
        final JsonActions jsonActions = actionConfigParser.parse(yaml);

        final JsonAction action = jsonActions.getActions().get(FILTER);
        final File resource = new File(this.getClass().getResource(JSON_EXAMPLE_JSON).getFile());

        final JsonTransformer underTest = new JsonTransformerImpl();

        //when
        final String actual = underTest.transform(resource, action);

        //then
        final String expected = IOUtils.resourceToString(JSON_EXAMPLE_FILTERED_JSON, StandardCharsets.UTF_8);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testTransformFileShouldFailWhenFileIsUnreachable() throws ConfigParseException {
        //given
        final InputStream yaml = this.getClass().getResourceAsStream(YAML_EXAMPLE_YML);
        final JsonActions jsonActions = actionConfigParser.parse(yaml);

        final JsonAction action = jsonActions.getActions().get(FILTER);
        final File resource = new File(FILTER);

        final JsonTransformer underTest = new JsonTransformerImpl();

        //when + then exception
        Assertions.assertThrows(JsonTransformException.class, () -> underTest.transform(resource, action));
    }

    @Test
    void testTransformStreamShouldFailWhenFileIsNOtJson() throws ConfigParseException {
        //given
        final InputStream yaml = this.getClass().getResourceAsStream(YAML_EXAMPLE_YML);
        final JsonActions jsonActions = actionConfigParser.parse(yaml);

        final JsonAction action = jsonActions.getActions().get(FILTER);
        final InputStream resource = this.getClass().getResourceAsStream(YAML_EXAMPLE_YML);

        final JsonTransformer underTest = new JsonTransformerImpl();

        //when + then exception
        Assertions.assertThrows(JsonTransformException.class, () -> underTest.transform(resource, action));
    }
}
