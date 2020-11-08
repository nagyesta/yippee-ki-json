package com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonCompositionSchemaTypeDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaObject;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.common.CommonStringValuesType;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Set;

import static com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.NamedComponentConverter.PROPERTY_NAME;
import static com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.CompositionType.*;
import static com.github.nagyesta.yippeekijson.metadata.schema.definitions.schema.JsonRefSchemaTypeDefinition.ref;
import static com.github.nagyesta.yippeekijson.test.helper.JsonTestUtil.jsonUtil;
import static com.github.nagyesta.yippeekijson.test.helper.TestResourceProvider.JSON_SCHEMA_ANY_SUPPLIER;
import static com.github.nagyesta.yippeekijson.test.helper.TestResourceProvider.resource;

@LaunchAbortArmed
@SpringBootTest
class JsonCompositionSchemaTypeDefinitionImplIntegrationTest {
    static final String REF = "ref";
    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void testCompositionOfSimpleAnyTypeWorksAsExpected() throws IOException {
        //given
        final JsonNode expected = resource().asJson(JSON_SCHEMA_ANY_SUPPLIER);

        //when
        final JsonCompositionSchemaTypeDefinition actual = JsonCompositionSchemaTypeDefinitionImpl.builder()
                .allOf(ImmutableList.<JsonSchemaObject>builder()
                        .add(JsonPropertiesSchemaTypeDefinition.builder()
                                .addRequiredProperty("name", CommonStringValuesType.builder()
                                        .addEnum("staticJson", "staticString")
                                        .build())
                                .build())
                        .add(JsonIfElseSchemaTypeDefinition.builder()
                                .ifNode(JsonPropertiesSchemaTypeDefinition.builder()
                                        .addProperty(PROPERTY_NAME, JsonConstantSchemaTypeDefinition.builder()
                                                .constant("staticJson")
                                                .build())
                                        .build())
                                .thenNode(ref("#/definitions/supplierTypes/definitions/supplierStaticJson"))
                                .build())
                        .add(JsonIfElseSchemaTypeDefinition.builder()
                                .ifNode(JsonPropertiesSchemaTypeDefinition.builder()
                                        .addProperty(PROPERTY_NAME, JsonConstantSchemaTypeDefinition.builder()
                                                .constant("staticString")
                                                .build())
                                        .build())
                                .thenNode(ref("#/definitions/supplierTypes/definitions/supplierStaticString"))
                                .build())
                        .build())
                .build();

        //then
        final JsonNode actualTree = jsonUtil().asNormalizedTree(actual);
        Assertions.assertEquals(expected, actualTree);
    }

    @Test
    void testCompositionOfSimpleAnyOfWorksAsExpected() {
        //given

        //when
        final JsonCompositionSchemaTypeDefinition actual = JsonCompositionSchemaTypeDefinitionImpl.builder()
                .anyOf(ImmutableList.<JsonSchemaObject>builder()
                        .add(ref(REF))
                        .build())
                .build();

        //then
        Assertions.assertEquals(Set.of(ANY_OF.getField()), actual.getComposition().keySet());
    }

    @Test
    void testCompositionOfSimpleOneOfOfWorksAsExpected() {
        //given

        //when
        final JsonCompositionSchemaTypeDefinition actual = JsonCompositionSchemaTypeDefinitionImpl.builder()
                .oneOf(ImmutableList.<JsonSchemaObject>builder()
                        .add(ref(REF))
                        .build())
                .build();

        //then
        Assertions.assertEquals(Set.of(ONE_OF.getField()), actual.getComposition().keySet());
    }

    @Test
    void testCompositionOfSimpleNotTypeWorksAsExpected() {
        //given

        //when
        final JsonCompositionSchemaTypeDefinition actual = JsonCompositionSchemaTypeDefinitionImpl.builder()
                .not(ref(REF))
                .build();

        //then
        Assertions.assertEquals(Set.of(NOT.getField()), actual.getComposition().keySet());
    }

}
