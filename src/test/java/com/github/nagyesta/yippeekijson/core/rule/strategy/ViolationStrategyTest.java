package com.github.nagyesta.yippeekijson.core.rule.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.networknt.schema.ExecutionContext;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Set;
import java.util.stream.Stream;

import static com.github.nagyesta.yippeekijson.test.helper.JsonTestUtil.jsonUtil;
import static com.github.nagyesta.yippeekijson.test.helper.TestResourceProvider.*;
import static org.mockito.Mockito.*;

@LaunchAbortArmed
class ViolationStrategyTest {

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
                .add(Arguments.of(resource().asString(JSON_VALIDATION_INPUT),
                        resource().asString(JSON_VALIDATION_OUTPUT), resource().asString(JSON_VALIDATION_TEST_SCHEMA)))
                .add(Arguments.of(TRIPLE_DOT_IN_KEY, TRIPLE_DOT_IN_KEY, resource().asString(JSON_VALIDATION_TEST_SCHEMA)))
                .add(Arguments.of(DOT_IN_KEY, DOT_IN_KEY_OUT, resource().asString(JSON_VALIDATION_TEST_SCHEMA)))
                .add(Arguments.of(ARRAY, ARRAY, resource().asString(JSON_VALIDATION_TEST_SCHEMA_INTEGER)))
                .build();
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
                                                      final String schema) {
        //given
        ViolationStrategy underTest = ViolationStrategy.COMMENT_JSON;
        final JsonNode rootNode = jsonUtil().readAsTree(jsonString);
        final JsonSchema jsonSchema = jsonUtil().asJsonSchema(schema);
        final ExecutionContext context = new ExecutionContext();
        final Set<ValidationMessage> validationMessages = jsonSchema
                .validate(context, rootNode, rootNode, ViolationStrategy.ROOT_NODE);
        final DocumentContext documentContext = JsonPath.parse(jsonString, Configuration.builder()
                .jsonProvider(new JacksonJsonProvider(jsonUtil().objectMapper()))
                .mappingProvider(new JacksonMappingProvider(jsonUtil().objectMapper()))
                .build());

        //when
        underTest.accept(documentContext, validationMessages);

        //then
        final String actualJson = documentContext.jsonString();
        final JsonNode actual = jsonUtil().readAsTree(actualJson);
        final JsonNode expected = jsonUtil().readAsTree(expectedJson);

        Assertions.assertEquals(expected, actual);
    }
}
