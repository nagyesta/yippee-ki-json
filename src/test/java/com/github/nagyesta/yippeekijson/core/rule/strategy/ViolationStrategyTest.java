package com.github.nagyesta.yippeekijson.core.rule.strategy;

import com.fasterxml.jackson.databind.*;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class ViolationStrategyTest {

    private static final String INPUT_JSON = "/validation/validation-input.json";
    private static final String TEST_SCHEMA_JSON = "/validation/test-schema.json";
    private static final String TEST_SCHEMA_INTEGER_JSON = "/validation/test-schema-integer.json";
    private static final String OUTPUT_JSON = "/validation/validation-output.json";
    private static final String TRIPLE_DOT_IN_KEY = "{\"object...array\":false}";
    private static final String DOT_IN_KEY = "{\"object.array\":false}";
    private static final String ARRAY = "[1,2,3]";
    private static final String DOT_IN_KEY_OUT = "{\"object.array\":false,\"$_yippee-schema-violation\":"
            + "[{\"path\":\"$.object.array\",\"message\":\"$.object.array: boolean found, number expected\"}]}";

    private static Stream<Arguments> logMessageProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(Set.of(mock(ValidationMessage.class))))
                .add(Arguments.of(Set.of()))
                .build();
    }

    private static Stream<Arguments> jsonProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(json(INPUT_JSON), json(OUTPUT_JSON), json(TEST_SCHEMA_JSON)))
                .add(Arguments.of(TRIPLE_DOT_IN_KEY, TRIPLE_DOT_IN_KEY, json(TEST_SCHEMA_JSON)))
                .add(Arguments.of(DOT_IN_KEY, DOT_IN_KEY_OUT, json(TEST_SCHEMA_JSON)))
                .add(Arguments.of(ARRAY, ARRAY, json(TEST_SCHEMA_INTEGER_JSON)))
                .build();
    }

    private static String json(final String jsonName) {
        try {
            return IOUtils.resourceToString(jsonName, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @ParameterizedTest
    @MethodSource("logMessageProvider")
    @NullSource
    void testLogOnlyAcceptShouldGetTheValidationMessages(final Set<ValidationMessage> validationMessages) {
        //given
        final ViolationStrategy underTest = ViolationStrategy.LOG_ONLY;
        final DocumentContext documentContext = mock(DocumentContext.class);

        //when
        underTest.accept(documentContext, validationMessages);

        //then
        verifyNoInteractions(documentContext);
        //noinspection ConstantConditions
        if (validationMessages != null) {
            for (final ValidationMessage m : validationMessages) {
                verify(m).getMessage();
                verifyNoMoreInteractions(m);
            }
        }
    }


    @ParameterizedTest
    @MethodSource("logMessageProvider")
    @NullSource
    void testIgnoreAcceptShouldIgnoreMessages(final Set<ValidationMessage> validationMessages) {
        //given
        final ViolationStrategy underTest = ViolationStrategy.IGNORE;
        final DocumentContext documentContext = mock(DocumentContext.class);

        //when
        underTest.accept(documentContext, validationMessages);

        //then
        verifyNoInteractions(documentContext);
        //noinspection ConstantConditions
        if (validationMessages != null) {
            for (final ValidationMessage m : validationMessages) {
                verifyNoInteractions(m);
            }
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testCommentAcceptShouldReturnIfEmptySetProvided(final Set<ValidationMessage> validationMessages) {
        //given
        final ViolationStrategy underTest = ViolationStrategy.COMMENT_JSON;
        final DocumentContext documentContext = mock(DocumentContext.class);

        //when
        underTest.accept(documentContext, validationMessages);

        //then
        verifyNoInteractions(documentContext);
    }

    @ParameterizedTest
    @MethodSource("jsonProvider")
    void testCommentAcceptShouldAddExtraNodesIntoJson(final String jsonString,
                                                      final String expectedJson,
                                                      final String schema) throws IOException {
        //given
        ViolationStrategy underTest = ViolationStrategy.COMMENT_JSON;
        final ObjectMapper objectMapper = objectMapper();
        final JsonNode rootNode = objectMapper.readTree(jsonString);
        JsonSchemaFactory factory = JsonSchemaFactory
                .builder(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7))
                .objectMapper(objectMapper)
                .build();
        final JsonSchema jsonSchema = factory.getSchema(schema);
        final Set<ValidationMessage> validationMessages = jsonSchema.validate(rootNode, rootNode, ViolationStrategy.ROOT_NODE);
        final DocumentContext documentContext = JsonPath.parse(jsonString, Configuration.builder()
                .jsonProvider(new JacksonJsonProvider(objectMapper))
                .mappingProvider(new JacksonMappingProvider(objectMapper))
                .build());

        //when
        underTest.accept(documentContext, validationMessages);

        //then
        final String actualJson = documentContext.jsonString();
        final JsonNode actual = objectMapper.readTree(actualJson);
        final JsonNode expected = objectMapper.readTree(expectedJson);

        Assertions.assertEquals(expected, actual);
    }

    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        final SerializationConfig serializationConfig = objectMapper.getSerializationConfig()
                .withoutFeatures(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.setConfig(serializationConfig);
        final DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig()
                .with(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .with(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
        objectMapper.setConfig(deserializationConfig);
        return objectMapper;
    }
}
