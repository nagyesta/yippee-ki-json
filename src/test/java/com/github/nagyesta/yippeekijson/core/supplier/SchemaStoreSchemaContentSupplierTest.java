package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.config.entities.SchemaStoreConfig;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.JsonMapperImpl;
import com.github.nagyesta.yippeekijson.core.http.HttpClient;
import com.github.nagyesta.yippeekijson.core.http.HttpRequestContext;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SchemaStoreSchemaContentSupplierTest {

    static final String YIPPEE_SCHEMA_NAME = "Yippee-Ki-JSON configuration YML";
    static final String CATALOG_URI = "http://localhost:41562/catalog.json";
    static final String YIPPR_SCHEMA_URI = "http://localhost:41562/nagyesta/yippee-ki-json/main/schema/yippee-ki-json_config_schema.json";
    private static final String SCHEMASTORE_CATALOG_JSON = "/validation/schemastore-catalog.json";
    private static final String YIPPEE_KI_JSON_CONFIG_SCHEMA_JSON = "/yippee-ki-json_config_schema.json";
    private static final String SCHEMA_ARRAY_PATH = "$.schemas[*]";
    private static final String NAME = "name";
    private static final String URL = "url";
    private static final String UNKNOWN = "unknown";

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null, null, null))
                .add(Arguments.of(YIPPEE_SCHEMA_NAME, null, null, null))
                .add(Arguments.of(null, mock(HttpClient.class), null, null))
                .add(Arguments.of(null, null, mock(JsonMapper.class), null))
                .add(Arguments.of(null, null, null, SchemaStoreConfig.builder().build()))
                .add(Arguments.of(YIPPEE_SCHEMA_NAME, mock(HttpClient.class), mock(JsonMapper.class), null))
                .add(Arguments.of(YIPPEE_SCHEMA_NAME, mock(HttpClient.class), null, SchemaStoreConfig.builder().build()))
                .add(Arguments.of(YIPPEE_SCHEMA_NAME, null, mock(JsonMapper.class), SchemaStoreConfig.builder().build()))
                .add(Arguments.of(null, mock(HttpClient.class), mock(JsonMapper.class), SchemaStoreConfig.builder().build()))
                .build();
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldNotAllowNulls(final String schemaName,
                                            final HttpClient httpClient,
                                            final JsonMapper jsonMapper,
                                            final SchemaStoreConfig schemaStoreConfig) {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new SchemaStoreSchemaContentSupplier(schemaName, httpClient, jsonMapper, schemaStoreConfig));
    }

    @Test
    void testGetShouldReturnSchemaWhenCalledWithKnownSchemaName() throws IOException {
        //given
        String storeCatalogJson = IOUtils.resourceToString(SCHEMASTORE_CATALOG_JSON, StandardCharsets.UTF_8);
        String schemaJson = IOUtils.resourceToString(YIPPEE_KI_JSON_CONFIG_SCHEMA_JSON, StandardCharsets.UTF_8);
        final HttpClient httpClient = mock(HttpClient.class);
        whenFetchedReturnJson(storeCatalogJson, httpClient, CATALOG_URI);
        whenFetchedReturnJson(schemaJson, httpClient, YIPPR_SCHEMA_URI);
        final JsonMapper jsonMapper = new JsonMapperImpl();
        final SchemaStoreConfig schemaStoreConfig = schemaStoreConfig();
        final SchemaStoreSchemaContentSupplier underTest = new SchemaStoreSchemaContentSupplier(
                YIPPEE_SCHEMA_NAME, httpClient, jsonMapper, schemaStoreConfig);

        //when
        final String actual = underTest.get();

        //then
        Assertions.assertEquals(schemaJson, actual);
    }

    @Test
    void testGetShouldThrowExceptionWhenCalledWithUnknownSchemaName() throws IOException {
        //given
        String storeCatalogJson = IOUtils.resourceToString(SCHEMASTORE_CATALOG_JSON, StandardCharsets.UTF_8);
        final HttpClient httpClient = mock(HttpClient.class);
        whenFetchedReturnJson(storeCatalogJson, httpClient, CATALOG_URI);
        final JsonMapper jsonMapper = new JsonMapperImpl();
        final SchemaStoreConfig schemaStoreConfig = schemaStoreConfig();
        final SchemaStoreSchemaContentSupplier underTest = new SchemaStoreSchemaContentSupplier(
                UNKNOWN, httpClient, jsonMapper, schemaStoreConfig);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, underTest::get);
    }

    @Test
    void testGetShouldThrowExceptionWhenCalledWithoutAnyKnownSchemas() throws IOException {
        //given
        String storeCatalogJson = IOUtils.resourceToString(SCHEMASTORE_CATALOG_JSON, StandardCharsets.UTF_8);
        final HttpClient httpClient = mock(HttpClient.class);
        whenFetchedReturnJson(storeCatalogJson, httpClient, CATALOG_URI);
        final JsonMapper jsonMapper = new JsonMapperImpl();
        final SchemaStoreConfig schemaStoreConfig = SchemaStoreConfig.builder()
                .catalogUri(CATALOG_URI)
                .schemaArrayPath(SCHEMA_ARRAY_PATH)
                .mappingNameKey(UNKNOWN)
                .mappingUrlKey(URL)
                .build();
        final SchemaStoreSchemaContentSupplier underTest = new SchemaStoreSchemaContentSupplier(
                YIPPEE_SCHEMA_NAME, httpClient, jsonMapper, schemaStoreConfig);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, underTest::get);
    }

    @Test
    void testGetShouldThrowExceptionWhenCatalogIsNotParsable() {
        //given
        final HttpClient httpClient = mock(HttpClient.class);
        whenFetchedReturnJson(UNKNOWN, httpClient, CATALOG_URI);
        final JsonMapper jsonMapper = new JsonMapperImpl();
        final SchemaStoreConfig schemaStoreConfig = SchemaStoreConfig.builder()
                .catalogUri(CATALOG_URI)
                .schemaArrayPath(SCHEMA_ARRAY_PATH)
                .mappingNameKey(UNKNOWN)
                .mappingUrlKey(URL)
                .build();
        final SchemaStoreSchemaContentSupplier underTest = new SchemaStoreSchemaContentSupplier(
                YIPPEE_SCHEMA_NAME, httpClient, jsonMapper, schemaStoreConfig);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, underTest::get);
    }

    @Test
    void testToStringShouldContainClassName() {
        //given
        final HttpClient httpClient = mock(HttpClient.class);
        final JsonMapper jsonMapper = new JsonMapperImpl();
        final SchemaStoreConfig schemaStoreConfig = SchemaStoreConfig.builder().build();
        final SchemaStoreSchemaContentSupplier underTest = new SchemaStoreSchemaContentSupplier(
                YIPPEE_SCHEMA_NAME, httpClient, jsonMapper, schemaStoreConfig);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(SchemaStoreSchemaContentSupplier.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(YIPPEE_SCHEMA_NAME));
    }

    private void whenFetchedReturnJson(final String storeCatalogJson, final HttpClient httpClient, final String catalogUri) {
        HttpRequestContext catalogRequestContext = HttpRequestContext.builder()
                .uri(catalogUri)
                .build();
        when(httpClient.fetch(eq(catalogRequestContext))).thenReturn(storeCatalogJson);
    }

    private SchemaStoreConfig schemaStoreConfig() {
        return SchemaStoreConfig.builder()
                .catalogUri(CATALOG_URI)
                .schemaArrayPath(SCHEMA_ARRAY_PATH)
                .mappingNameKey(NAME)
                .mappingUrlKey(URL)
                .build();
    }
}
