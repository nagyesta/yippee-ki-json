package com.github.nagyesta.yippeekijson.metadata.schema.definitions.registry;

import com.github.nagyesta.yippeekijson.metadata.schema.definitions.converter.NamedComponentConverter;
import com.github.nagyesta.yippeekijson.metadata.schema.parser.ComponentContextMetadataParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AbstractComponentTypeDefinitionRegistryIntegrationTest {

    @Autowired
    private NamedComponentConverter namedFunctionalComponentConverter;
    @Autowired
    private ComponentContextMetadataParser componentContextMetadataParser;

    @Test
    void testParseAndRegisterShouldThrowExceptionIfInvalidTypeProvided() {
        //given
        Class<?> input = AbstractComponentTypeDefinitionRegistryIntegrationTest.class;
        final AbstractComponentTypeDefinitionRegistry underTest = new SupplierComponentTypeDefinitionRegistry(
                componentContextMetadataParser, namedFunctionalComponentConverter);

        //when
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            underTest.parseAndRegister(input);
        });
    }
}
