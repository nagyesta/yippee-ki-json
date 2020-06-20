package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.annotation.MapParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.http.HttpClient;
import com.github.nagyesta.yippeekijson.core.http.HttpRequestContext;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * {@link Supplier} returning the whole content of a web resource.
 */
@Slf4j
public final class HttpResourceContentSupplier implements Supplier<String> {

    static final String NAME = "httpResource";

    private final HttpRequestContext httpRequestContext;
    private final HttpClient httpClient;

    @NamedSupplier(NAME)
    public HttpResourceContentSupplier(@ValueParam @NonNull final String uri,
                                       @ValueParam @Nullable final String httpMethod,
                                       @MapParam @Nullable final Map<String, String> httpHeaders,
                                       @ValueParam @Nullable final String charset,
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
