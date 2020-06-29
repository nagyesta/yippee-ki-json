package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.JsonAction;
import com.github.nagyesta.yippeekijson.core.config.entities.JsonActions;
import com.github.nagyesta.yippeekijson.core.config.parser.ActionConfigParser;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import com.github.nagyesta.yippeekijson.core.exception.JsonTransformException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

@SpringBootTest
class JsonTransformerImplIntegrationTest {

    private static final String YAML_EXAMPLE_YML = "/yaml/example.yml";
    private static final String JSON_EXAMPLE_JSON = "/json/example.json";
    private static final String JSON_EXAMPLE_FILTERED_JSON = "/json/example-filtered.json";
    private static final String FILTER = "filter";
    @Autowired
    private ActionConfigParser actionConfigParser;
    @Autowired
    private JsonMapper jsonMapper;

    private static Stream<Arguments> nullStreamProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null, null))
                .add(Arguments.of(InputStream.nullInputStream(), null, null))
                .add(Arguments.of(null, StandardCharsets.UTF_8, null))
                .add(Arguments.of(null, null, JsonAction.builder().build()))
                .add(Arguments.of(InputStream.nullInputStream(), StandardCharsets.UTF_8, null))
                .add(Arguments.of(InputStream.nullInputStream(), null, JsonAction.builder().build()))
                .add(Arguments.of(null, StandardCharsets.UTF_8, JsonAction.builder().build()))
                .build();
    }


    private static Stream<Arguments> nullFileProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null, null))
                .add(Arguments.of(mock(File.class), null, null))
                .add(Arguments.of(null, StandardCharsets.UTF_8, null))
                .add(Arguments.of(null, null, JsonAction.builder().build()))
                .add(Arguments.of(mock(File.class), StandardCharsets.UTF_8, null))
                .add(Arguments.of(mock(File.class), null, JsonAction.builder().build()))
                .add(Arguments.of(null, StandardCharsets.UTF_8, JsonAction.builder().build()))
                .build();
    }

    @Test
    void testConstructorShouldNotAllowNull() {
        //given
        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new JsonTransformerImpl(null));
    }

    @ParameterizedTest
    @MethodSource("nullStreamProvider")
    void testTransformShouldNotAllowNulls(final InputStream inputStream,
                                          final Charset charset,
                                          final JsonAction action) {
        //given
        final JsonTransformer underTest = new JsonTransformerImpl(jsonMapper);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> underTest.transform(inputStream, charset, action));
    }

    @ParameterizedTest
    @MethodSource("nullFileProvider")
    void testTransformShouldNotAllowNulls(final File file,
                                          final Charset charset,
                                          final JsonAction action) {
        //given
        final JsonTransformer underTest = new JsonTransformerImpl(jsonMapper);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> underTest.transform(file, charset, action));
    }

    @Test
    void testTransformStreamShouldProcessValidInput() throws ConfigParseException, JsonTransformException, IOException {
        //given
        final InputStream yaml = this.getClass().getResourceAsStream(YAML_EXAMPLE_YML);
        final JsonActions jsonActions = actionConfigParser.parse(yaml, true);

        final JsonAction action = jsonActions.getActions().get(FILTER);
        final InputStream resource = this.getClass().getResourceAsStream(JSON_EXAMPLE_JSON);

        final JsonTransformer underTest = new JsonTransformerImpl(jsonMapper);

        //when
        final String actual = underTest.transform(resource, StandardCharsets.UTF_8, action);

        //then
        final String expected = IOUtils.resourceToString(JSON_EXAMPLE_FILTERED_JSON, StandardCharsets.UTF_8);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testTransformStreamShouldHandleEmptyRuleSet() throws JsonTransformException, IOException {
        //given
        final JsonAction action = JsonAction.builder().name(FILTER).build();
        final InputStream resource = this.getClass().getResourceAsStream(JSON_EXAMPLE_JSON);

        final JsonTransformer underTest = new JsonTransformerImpl(jsonMapper);

        //when
        final String actual = underTest.transform(resource, StandardCharsets.UTF_8, action);

        //then
        final String expected = IOUtils.resourceToString(JSON_EXAMPLE_JSON, StandardCharsets.UTF_8);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testTransformFileShouldProcessValidInput() throws ConfigParseException, JsonTransformException, IOException {
        //given
        final InputStream yaml = this.getClass().getResourceAsStream(YAML_EXAMPLE_YML);
        final JsonActions jsonActions = actionConfigParser.parse(yaml, true);

        final JsonAction action = jsonActions.getActions().get(FILTER);
        final File resource = new File(this.getClass().getResource(JSON_EXAMPLE_JSON).getFile());

        final JsonTransformer underTest = new JsonTransformerImpl(jsonMapper);

        //when
        final String actual = underTest.transform(resource, StandardCharsets.UTF_8, action);

        //then
        final String expected = IOUtils.resourceToString(JSON_EXAMPLE_FILTERED_JSON, StandardCharsets.UTF_8);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testTransformFileShouldFailWhenFileIsUnreachable() throws ConfigParseException {
        //given
        final InputStream yaml = this.getClass().getResourceAsStream(YAML_EXAMPLE_YML);
        final JsonActions jsonActions = actionConfigParser.parse(yaml, false);

        final JsonAction action = jsonActions.getActions().get(FILTER);
        final File resource = new File(FILTER);

        final JsonTransformer underTest = new JsonTransformerImpl(jsonMapper);

        //when + then exception
        Assertions.assertThrows(JsonTransformException.class,
                () -> underTest.transform(resource, StandardCharsets.UTF_8, action));
    }

    @Test
    void testTransformStreamShouldFailWhenFileIsNotJson() throws ConfigParseException {
        //given
        final InputStream yaml = this.getClass().getResourceAsStream(YAML_EXAMPLE_YML);
        final JsonActions jsonActions = actionConfigParser.parse(yaml, false);

        final JsonAction action = jsonActions.getActions().get(FILTER);
        final InputStream resource = this.getClass().getResourceAsStream(YAML_EXAMPLE_YML);

        final JsonTransformer underTest = new JsonTransformerImpl(jsonMapper);

        //when + then exception
        Assertions.assertThrows(JsonTransformException.class,
                () -> underTest.transform(resource, StandardCharsets.UTF_8, action));
    }
}
