package com.github.nagyesta.yippeekijson.test.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.JsonMapperImpl;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.io.InputStream;

@LaunchAbortArmed
public final class JsonTestUtil {
    private final JsonMapperImpl jsonMapper = new JsonMapperImpl();
    private final ObjectMapper objectMapper = jsonMapper.objectMapper();

    private JsonTestUtil() {
        //singleton
    }

    public static JsonTestUtil jsonUtil() {
        return JsonTestUtilHolder.INSTANCE;
    }

    public JsonNode asNormalizedTree(final Object input) throws JsonProcessingException {
        return objectMapper.readTree(objectMapper.writeValueAsString(input));
    }

    public JsonSchema asJsonSchema(final String asString) {
        JsonSchemaFactory factory = JsonSchemaFactory
                .builder(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7))
                .objectMapper(jsonMapper.objectMapper())
                .build();
        return factory.getSchema(asString);
    }

    public JsonNode readAsTree(final String asString) {
        try {
            return objectMapper.readTree(asString);
        } catch (final IOException e) {
            Assertions.fail(e);
            return null;
        }
    }

    public JsonNode readAsTree(final InputStream asStream) {
        try {
            return objectMapper.readTree(asStream);
        } catch (final IOException e) {
            Assertions.fail(e);
            return null;
        }
    }

    public DocumentContext readAsDocumentContext(final String asString) {
        return JsonPath.parse(asString, jsonMapper.parserConfiguration());
    }

    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    private static final class JsonTestUtilHolder {
        private static final JsonTestUtil INSTANCE = new JsonTestUtil();
    }
}
