package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.config.entities.SchemaStoreConfig;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.http.HttpClient;
import com.github.nagyesta.yippeekijson.core.http.HttpRequestContext;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;

import java.net.URI;
import java.util.*;
import java.util.function.Supplier;

import static com.google.common.net.HttpHeaders.ACCEPT;

@Slf4j
public class SchemaStoreSchemaContentSupplier implements Supplier<String> {

    private static final String NAME = "schemaStore";

    private final JsonMapper jsonMapper;
    private final String schemaName;
    private final HttpClient httpClient;
    private final SchemaStoreConfig schemaStoreConfig;

    @NamedSupplier(NAME)
    public SchemaStoreSchemaContentSupplier(@ValueParam @NonNull final String schemaName,
                                            @NonNull final HttpClient httpClient,
                                            @NonNull final JsonMapper jsonMapper,
                                            @NonNull final SchemaStoreConfig schemaStoreConfig) {
        this.jsonMapper = jsonMapper;
        this.schemaName = schemaName;
        this.httpClient = httpClient;
        this.schemaStoreConfig = schemaStoreConfig;
    }

    @Override
    public String get() {
        final Optional<URI> uri = Optional.ofNullable(fetchDescriptor().get(schemaName));
        Assert.isTrue(uri.isPresent(), "Failed to find URI for SchemaStore schema: '" + schemaName + "'");
        final HttpRequestContext requestContext = HttpRequestContext.builder()
                .uri(uri.get().toString())
                .addHeader(ACCEPT, MimeTypeUtils.ALL_VALUE)
                .build();
        return httpClient.fetch(requestContext);
    }

    private Map<String, URI> fetchDescriptor() {
        final HttpRequestContext requestContext = HttpRequestContext.builder()
                .uri(schemaStoreConfig.getCatalogUri())
                .addHeader(ACCEPT, MimeTypeUtils.ALL_VALUE)
                .build();
        final String schemaStoreCatalog = httpClient.fetch(requestContext);
        Map<String, URI> schemaCatalog = new HashMap<>();
        try {
            final List<Object> result = JsonPath.parse(schemaStoreCatalog,
                    Configuration.builder().options(Option.ALWAYS_RETURN_LIST).build())
                    .read(schemaStoreConfig.getSchemaArrayPath());
            result.forEach(schemaItem -> {
                final Map<String, Object> map = jsonMapper.mapTo(schemaItem, JsonMapper.MapTypeRef.INSTANCE);
                try {
                    schemaCatalog.put(nonNullString(map.get(schemaStoreConfig.getMappingNameKey())),
                            URI.create(nonNullString(map.get(schemaStoreConfig.getMappingUrlKey()))));
                } catch (final Exception e) {
                    log.warn("Invalid schema item found: " + map);
                }
            });
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
        return schemaCatalog;
    }

    @NotNull
    private String nonNullString(final Object obj) {
        Assert.notNull(obj, "object value cannot be null.");
        final String string = StringUtils.trimToNull(obj.toString());
        Assert.notNull(string, "string value cannot be null.");
        return string;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SchemaStoreSchemaContentSupplier.class.getSimpleName() + "[", "]")
                .add("schemaName='" + schemaName + "'")
                .toString();
    }
}
