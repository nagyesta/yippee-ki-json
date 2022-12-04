package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.annotation.MapParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.http.HttpClient;
import com.github.nagyesta.yippeekijson.core.http.HttpMethod;
import com.github.nagyesta.yippeekijson.core.http.HttpRequestContext;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.*;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * {@link Supplier} returning the whole content of a web resource.
 */
@Slf4j
public final class HttpResourceContentSupplier implements Supplier<String> {

    static final String NAME = "httpResource";
    static final String PARAM_HTTP_HEADERS = "httpHeaders";

    private final HttpRequestContext httpRequestContext;
    private final HttpClient httpClient;

    @SchemaDefinition(
            outputType = String.class,
            properties = @PropertyDefinitions(
                    @PropertyDefinition(name = PARAM_HTTP_HEADERS,
                            type = @TypeDefinition(
                                    itemType = Map.class, itemTypeParams = {String.class, String.class}
                            ),
                            commonTypeRef = "#/definitions/commonTypes/definitions/httpHeaders"
                    )
            ),
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_SUPPLIERS, section = "HTTP resource content supplier"),
            sinceVersion = WikiConstants.VERSION_1_2_0,
            description = {
                    "This supplier returns the contents of an HTTP resource as text."
            },
            example = @Example(
                    in = "/examples/json/http-download-input.json",
                    out = "/examples/json/http-download-output.json",
                    yml = "/examples/yml/http-download.yml",
                    skipTest = true,
                    note = "In this example we have downloaded a file and saved it's contents.")
    )
    @NamedSupplier(NAME)
    public HttpResourceContentSupplier(@ValueParam(docs = "The URI we want to send the request to.")
                                       @NonNull final String uri,
                                       @ValueParam(docs = "The HTTP request method we want to use. Defaults to GET.")
                                       @Nullable final HttpMethod httpMethod,
                                       @MapParam(docs = "HTTP headers we want to send with our request.")
                                       @Nullable final Map<String, String> httpHeaders,
                                       @ValueParam(docs = "The charset used for reading the contents of the response. Defaults to UTF-8.")
                                       @Nullable final Charset charset,
                                       @NonNull final HttpClient httpClient) {
        this.httpRequestContext = new HttpRequestContext(uri, httpMethod, httpHeaders, charset);
        this.httpClient = httpClient;
    }

    @Override
    public String get() {
        return httpClient.fetch(httpRequestContext);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HttpResourceContentSupplier.class.getSimpleName() + "[", "]")
                .add("httpRequestContext=" + httpRequestContext)
                .toString();
    }
}
