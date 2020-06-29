package com.github.nagyesta.yippeekijson.core.http;

import com.google.common.net.HttpHeaders;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Entity class containing vital input parameters for an {@link HttpRequest}.
 */
@Getter
public final class HttpRequestContext {

    private final String uri;
    private final HttpMethod httpMethod;
    private final Map<String, List<String>> headers;
    private final Charset charset;

    public HttpRequestContext(@Nullable final String uri,
                              @Nullable final String method,
                              @Nullable final Map<String, String> headers,
                              @Nullable final String charset) {
        this.uri = uri;
        this.httpMethod = Optional.ofNullable(method).map(HttpMethod::valueOf).orElse(HttpRequestContext.HttpMethod.GET);
        this.headers = new TreeMap<>();
        Optional.ofNullable(headers).orElse(Collections.emptyMap())
                .forEach((k, v) -> this.headers.put(k, List.of(v)));
        this.charset = Optional.ofNullable(charset).map(Charset::forName).orElse(StandardCharsets.UTF_8);
        this.headers.putIfAbsent(HttpHeaders.ACCEPT_CHARSET, List.of(this.charset.name()));
    }

    private HttpRequestContext(final HttpRequestContextBuilder builder) {
        this.uri = builder.uri;
        this.httpMethod = builder.httpMethod;
        this.headers = new TreeMap<>();
        builder.headers.forEach((k, v) -> headers.put(k, List.copyOf(v)));
        this.charset = builder.charset;
        if (charset != null) {
            this.headers.putIfAbsent(HttpHeaders.ACCEPT_CHARSET, List.of(this.charset.name()));
        }
    }

    public static HttpRequestContextBuilder builder() {
        return new HttpRequestContextBuilder();
    }

    public HttpRequestContext withOverrides(final HttpRequestContext overrides) {
        final HttpRequestContextBuilder contextBuilder = builder()
                .uri(Objects.requireNonNullElse(overrides.getUri(), this.uri))
                .httpMethod(Objects.requireNonNullElse(overrides.getHttpMethod(), this.httpMethod))
                .charset(Objects.requireNonNullElse(overrides.getCharset(), this.charset));
        this.headers.forEach((k, v) -> overrides.getHeaders().getOrDefault(k, v)
                .forEach(listItem -> contextBuilder.addHeader(k, listItem)));
        overrides.getHeaders().entrySet().stream()
                .filter(e -> !this.headers.containsKey(e.getKey()))
                .forEach(e -> e.getValue()
                        .forEach(listItem -> contextBuilder.addHeader(e.getKey(), listItem)));
        return contextBuilder.build();
    }

    public HttpRequest.Builder toHttpRequestBuilder() {
        final HttpRequest.Builder builder = HttpRequest.newBuilder()
                .method(this.httpMethod.name(), HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(this.uri));
        headers.forEach((name, list) -> list.forEach(value -> builder.header(name, value)));
        return builder;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HttpRequestContext.class.getSimpleName() + "[", "]")
                .add("uri='" + uri + "'")
                .add("httpMethod='" + httpMethod + "'")
                .add("charset='" + charset.name() + "'")
                .add("headers=" + headers)
                .toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpRequestContext)) {
            return false;
        }

        HttpRequestContext that = (HttpRequestContext) o;

        return new EqualsBuilder()
                .append(uri, that.uri)
                .append(httpMethod, that.httpMethod)
                .append(headers, that.headers)
                .append(charset, that.charset)
                .isEquals();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(uri)
                .append(httpMethod)
                .append(headers)
                .append(charset)
                .toHashCode();
    }

    public enum HttpMethod {
        /**
         * HTTP GET method.
         */
        GET,
        /**
         * HTTP POST method.
         */
        POST
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static class HttpRequestContextBuilder {
        private String uri;
        private HttpMethod httpMethod;
        private Map<String, List<String>> headers;
        private Charset charset;

        HttpRequestContextBuilder() {
            reset();
        }

        private void reset() {
            this.uri = null;
            this.httpMethod = HttpMethod.GET;
            this.headers = new TreeMap<>();
            this.charset = StandardCharsets.UTF_8;
        }

        public HttpRequestContextBuilder uri(@Nullable final String uri) {
            this.uri = uri;
            return this;
        }

        public HttpRequestContextBuilder httpMethod(@NotNull final HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public HttpRequestContextBuilder httpMethod(@Nullable final String httpMethod) {
            this.httpMethod = Optional.ofNullable(httpMethod).map(HttpMethod::valueOf).orElse(null);
            return this;
        }

        public HttpRequestContextBuilder addHeader(@NotNull final String name,
                                                   @NotNull final String value) {
            final List<String> stringList = headers.getOrDefault(name, new ArrayList<>());
            stringList.add(value);
            this.headers.put(name, stringList);
            return this;
        }

        public HttpRequestContextBuilder addHeaders(@NotNull final Map<String, String> headers) {
            headers.forEach(this::addHeader);
            return this;
        }

        public HttpRequestContextBuilder charset(@Nullable final Charset charset) {
            this.charset = charset;
            return this;
        }

        public HttpRequestContext build() {
            final HttpRequestContext requestContext = new HttpRequestContext(this);
            this.reset();
            return requestContext;
        }
    }
}
