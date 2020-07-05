package com.github.nagyesta.yippeekijson.metadata.schema.definitions.registry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonCommonTypeDefinitionRegistry;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaTypeDefinition;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@SpringBootTest
class JsonCommonTypeDefinitionRegistryImplIntegrationTest {

    @Autowired
    private JsonCommonTypeDefinitionRegistry underTest;
    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void testCommonTypesAreRegisteredAsExpected() throws IOException {
        //given
        final ObjectMapper objectMapper = jsonMapper.objectMapper();
        final String schemaString = IOUtils.resourceToString("/schema/common-types.json", StandardCharsets.UTF_8);
        final JsonNode expected = objectMapper.readTree(schemaString);

        //when
        final Map<String, JsonSchemaTypeDefinition> actual = underTest.registeredDefinitions();

        //then
        final String jsonActual = objectMapper.writeValueAsString(actual);
        final JsonNode actualTree = objectMapper.readTree(jsonActual);
        Assertions.assertEquals(expected, actualTree);
    }
}
