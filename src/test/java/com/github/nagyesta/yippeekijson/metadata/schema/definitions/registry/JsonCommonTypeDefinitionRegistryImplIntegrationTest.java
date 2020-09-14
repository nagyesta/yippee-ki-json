package com.github.nagyesta.yippeekijson.metadata.schema.definitions.registry;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonCommonTypeDefinitionRegistry;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaTypeDefinition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;

import static com.github.nagyesta.yippeekijson.test.helper.JsonTestUtil.jsonUtil;
import static com.github.nagyesta.yippeekijson.test.helper.TestResourceProvider.JSON_SCHEMA_COMMON_TYPES;
import static com.github.nagyesta.yippeekijson.test.helper.TestResourceProvider.resource;

@SpringBootTest
class JsonCommonTypeDefinitionRegistryImplIntegrationTest {

    @Autowired
    private JsonCommonTypeDefinitionRegistry underTest;

    @Test
    void testCommonTypesAreRegisteredAsExpected() throws IOException {
        //given
        final JsonNode expected = resource().asJson(JSON_SCHEMA_COMMON_TYPES);

        //when
        final Map<String, JsonSchemaTypeDefinition> actual = underTest.registeredDefinitions();

        //then
        final JsonNode actualTree = jsonUtil().asNormalizedTree(actual);
        Assertions.assertEquals(expected, actualTree);
    }
}
