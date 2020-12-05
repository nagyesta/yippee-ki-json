package com.github.nagyesta.yippeekijson.test.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.jayway.jsonpath.DocumentContext;
import com.networknt.schema.JsonSchema;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.github.nagyesta.yippeekijson.test.helper.JsonTestUtil.jsonUtil;

@LaunchAbortArmed
@SuppressWarnings("checkstyle:JavadocVariable")
public final class TestResourceProvider {
    public static final String YIPPEE_KI_JSON_CONFIG_SCHEMA_JSON = "/yippee-ki-json_config_schema.json";
    public static final String JSON_EXAMPLE = "/json/example.json";
    public static final String JSON_EXAMPLE_FILTERED = "/json/example-filtered.json";
    public static final String JSON_EXAMPLE_FILTERED_SPLIT = "/json/example-filtered-split.json";
    public static final String JSON_EXAMPLE_SPLIT = "/json/example-split.json";
    public static final String JSON_SCHEMA_ANY_SUPPLIER = "/schema/any-supplier.json";
    public static final String JSON_SCHEMA_COMMON_TYPES = "/schema/common-types.json";
    public static final String JSON_VALIDATION_SCHEMA_CATALOGUE = "/validation/schemastore-catalog.json";
    public static final String JSON_VALIDATION_TEST_SCHEMA = "/validation/test-schema.json";
    public static final String JSON_VALIDATION_TEST_SCHEMA_INTEGER = "/validation/test-schema-integer.json";
    public static final String JSON_VALIDATION_INPUT = "/validation/validation-input.json";
    public static final String JSON_VALIDATION_OUTPUT = "/validation/validation-output.json";
    public static final String MD_MINIMAL_DOCS = "/markdown/minimal-docs.md";
    public static final String YML_ALL_RULES = "/yaml/all-rules.yml";
    public static final String YML_EXAMPLE = "/yaml/example.yml";
    public static final String YML_EXAMPLE_INVALID = "/yaml/example-invalid.yml";
    public static final String YML_INVALID = "/yaml/invalid.yml";
    public static final String YML_MULTI_LEVEL = "/yaml/multi-level.yml";
    public static final String YML_SCHEMA_STORE = "/yaml/schema-store.yml";

    private TestResourceProvider() {
        //singleton
    }

    public static TestResourceProvider resource() {
        return TestResourceProviderHolder.INSTANCE;
    }

    public JsonNode asJson(final String resourcePath) {
        return jsonUtil().readAsTree(asStream(resourcePath));
    }

    public DocumentContext asDocumentContext(final String resourcePath) {
        return jsonUtil().readAsDocumentContext(asString(resourcePath));
    }

    public InputStream asStream(final String resourcePath) {
        return TestResourceProvider.class.getResourceAsStream(resourcePath);
    }

    public JsonSchema asJsonSchema(final String resourcePath) {
        return jsonUtil().asJsonSchema(asString(resourcePath));
    }

    public String asString(final String resourcePath) {
        try {
            return IOUtils.resourceToString(resourcePath, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            Assertions.fail(e);
            return null;
        }
    }

    public File asFile(final String resourcePath) {
        return new File(this.getClass().getResource(resourcePath).getFile());
    }

    private static final class TestResourceProviderHolder {
        private static final TestResourceProvider INSTANCE = new TestResourceProvider();
    }
}
