package com.github.nagyesta.yippeekijson.core.function.helper;

import com.github.nagyesta.yippeekijson.core.http.HttpClient;
import com.github.nagyesta.yippeekijson.core.http.HttpRequestContext;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * {@link Function} allowing us to fetch resources over HTTP.
 *
 * @param <T> The type of the input data we have
 */
public abstract class HttpResourceContentSupport<T> implements Function<T, String> {

    private final HttpRequestContext httpRequestContext;
    private final HttpClient httpClient;

    public HttpResourceContentSupport(@NonNull final HttpClient httpClient,
                                      @Nullable final String uri,
                                      @Nullable final String method,
                                      @Nullable final Map<String, String> headers,
                                      @Nullable final String charset) {
        this.httpRequestContext = new HttpRequestContext(uri, method, headers, charset);
        this.httpClient = httpClient;
    }


    @Override
    public String apply(final T override) {
        final HttpRequestContext overrides = buildOverrideContext(override);
        return httpClient.fetch(httpRequestContext, overrides);
    }

    /**
     * Generates the override context for the HTTP call to let us merge and fetch based on it.
     *
     * @param override The override values we should use for the context creation.
     * @return An {@link HttpRequestContext} containing all of the override values.
     */
    @NotNull
    protected abstract HttpRequestContext buildOverrideContext(T override);

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("httpRequestContext=" + httpRequestContext)
                .toString();
    }
}
