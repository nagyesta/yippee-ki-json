package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.config.entities.SchemaStoreConfig;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.JsonMapperImpl;
import com.github.nagyesta.yippeekijson.core.http.HttpClient;
import com.github.nagyesta.yippeekijson.core.http.HttpRequestContext;
import com.google.common.net.HttpHeaders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.util.MimeTypeUtils;

import java.util.stream.Stream;

import static com.github.nagyesta.yippeekijson.test.helper.TestResourceProvider.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@LaunchAbortArmed
class SchemaStoreSchemaContentSupplierTest {

    static final String YIPPEE_SCHEMA_NAME = "Yippee-Ki-JSON configuration YML";
    static final String CATALOG_URI = "http://localhost:41562/catalog.json";
    static final String YIPPEE_SCHEMA_URI = "http://localhost:41562/nagyesta/yippee-ki-json/main/schema/yippee-ki-json_config_schema.json";
    private static final String SCHEMA_ARRAY_PATH = "$.schemas[*]";
    private static final String NAME = "name";
    private static final String URL = "url";
    private static final String UNKNOWN = "unknown";
    private static final SchemaStoreConfig EMPTY_SCHEMA_STORE_CONFIG = SchemaStoreConfig.builder().build();
    private static String storeCatalogJson;
    private static String schemaJson;
    private static JsonMapper jsonMapper;
    private static SchemaStoreConfig schemaStoreConfig;

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null, null, null))
                .add(Arguments.of(YIPPEE_SCHEMA_NAME, null, null, null))
                .add(Arguments.of(null, mock(HttpClient.class), null, null))
                .add(Arguments.of(null, null, mock(JsonMapper.class), null))
                .add(Arguments.of(null, null, null, EMPTY_SCHEMA_STORE_CONFIG))
                .add(Arguments.of(YIPPEE_SCHEMA_NAME, mock(HttpClient.class), mock(JsonMapper.class), null))
                .add(Arguments.of(YIPPEE_SCHEMA_NAME, mock(HttpClient.class), null, EMPTY_SCHEMA_STORE_CONFIG))
                .add(Arguments.of(YIPPEE_SCHEMA_NAME, null, mock(JsonMapper.class), EMPTY_SCHEMA_STORE_CONFIG))
                .add(Arguments.of(null, mock(HttpClient.class), mock(JsonMapper.class), EMPTY_SCHEMA_STORE_CONFIG))
                .build();
    }

    @BeforeAll
    static void beforeAll() {
        storeCatalogJson = resource().asString(JSON_VALIDATION_SCHEMA_CATALOGUE);
        schemaJson = resource().asString(YIPPEE_KI_JSON_CONFIG_SCHEMA_JSON);
        jsonMapper = new JsonMapperImpl();
        schemaStoreConfig = SchemaStoreConfig.builder()
                .catalogUri(CATALOG_URI)
                .schemaArrayPath(SCHEMA_ARRAY_PATH)
                .mappingNameKey(NAME)
                .mappingUrlKey(URL)
                .build();
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldNotAllowNulls(final String schemaName,
                                            final HttpClient httpClient,
                                            final JsonMapper jsonMapperParam,
                                            final SchemaStoreConfig schemaStoreConfigParam) {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new SchemaStoreSchemaContentSupplier(schemaName, httpClient, jsonMapperParam, schemaStoreConfigParam));
    }

    @Test
    void testGetShouldReturnSchemaWhenCalledWithKnownSchemaName() {
        //given
        final HttpClient httpClient = mock(HttpClient.class);
        whenFetchedReturnJson(storeCatalogJson, httpClient, CATALOG_URI);
        whenFetchedReturnJson(schemaJson, httpClient, YIPPEE_SCHEMA_URI);
        final SchemaStoreSchemaContentSupplier underTest = new SchemaStoreSchemaContentSupplier(
                YIPPEE_SCHEMA_NAME, httpClient, jsonMapper, schemaStoreConfig);

        //when
        final String actual = underTest.get();

        //then
        Assertions.assertEquals(schemaJson, actual);
    }

    @Test
    void testGetShouldThrowExceptionWhenCalledWithUnknownSchemaName() {
        //given
        final HttpClient httpClient = mock(HttpClient.class);
        whenFetchedReturnJson(storeCatalogJson, httpClient, CATALOG_URI);
        final SchemaStoreSchemaContentSupplier underTest = new SchemaStoreSchemaContentSupplier(
                UNKNOWN, httpClient, jsonMapper, schemaStoreConfig);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, underTest::get);
    }

    @Test
    void testGetShouldThrowExceptionWhenCalledWithoutAnyKnownSchemas() {
        //given
        final HttpClient httpClient = mock(HttpClient.class);
        whenFetchedReturnJson(storeCatalogJson, httpClient, CATALOG_URI);
        final SchemaStoreConfig customSchemaStoreConfig = SchemaStoreConfig.builder()
                .catalogUri(CATALOG_URI)
                .schemaArrayPath(SCHEMA_ARRAY_PATH)
                .mappingNameKey(UNKNOWN)
                .mappingUrlKey(URL)
                .build();
        final SchemaStoreSchemaContentSupplier underTest = new SchemaStoreSchemaContentSupplier(
                YIPPEE_SCHEMA_NAME, httpClient, jsonMapper, customSchemaStoreConfig);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, underTest::get);
    }

    @Test
    void testGetShouldThrowExceptionWhenCatalogIsNotParsable() {
        //given
        final HttpClient httpClient = mock(HttpClient.class);
        whenFetchedReturnJson(UNKNOWN, httpClient, CATALOG_URI);
        final SchemaStoreConfig customSchemaStoreConfig = SchemaStoreConfig.builder()
                .catalogUri(CATALOG_URI)
                .schemaArrayPath(SCHEMA_ARRAY_PATH)
                .mappingNameKey(UNKNOWN)
                .mappingUrlKey(URL)
                .build();
        final SchemaStoreSchemaContentSupplier underTest = new SchemaStoreSchemaContentSupplier(
                YIPPEE_SCHEMA_NAME, httpClient, jsonMapper, customSchemaStoreConfig);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, underTest::get);
    }

    @Test
    void testToStringShouldContainClassName() {
        //given
        final HttpClient httpClient = mock(HttpClient.class);
        final SchemaStoreSchemaContentSupplier underTest = new SchemaStoreSchemaContentSupplier(
                YIPPEE_SCHEMA_NAME, httpClient, jsonMapper, EMPTY_SCHEMA_STORE_CONFIG);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(SchemaStoreSchemaContentSupplier.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(YIPPEE_SCHEMA_NAME));
    }

    private void whenFetchedReturnJson(final String json, final HttpClient httpClient, final String uri) {
        HttpRequestContext requestContext = HttpRequestContext.builder()
                .uri(uri)
                .addHeader(HttpHeaders.ACCEPT, MimeTypeUtils.ALL_VALUE)
                .build();
        when(httpClient.fetch(eq(requestContext))).thenReturn(json);
    }
}
