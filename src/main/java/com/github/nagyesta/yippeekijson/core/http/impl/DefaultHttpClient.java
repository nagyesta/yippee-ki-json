package com.github.nagyesta.yippeekijson.core.http.impl;

import com.github.nagyesta.yippeekijson.core.annotation.Injectable;
import com.github.nagyesta.yippeekijson.core.config.entities.HttpConfig;
import com.github.nagyesta.yippeekijson.core.exception.AbortTransformationException;
import com.github.nagyesta.yippeekijson.core.http.HttpClient;
import com.github.nagyesta.yippeekijson.core.http.HttpRequestContext;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static com.google.common.net.HttpHeaders.ACCEPT;
import static com.google.common.net.HttpHeaders.USER_AGENT;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Slf4j
@Injectable(forType = HttpClient.class)
public class DefaultHttpClient implements HttpClient {

    private final HttpConfig httpConfig;

    public DefaultHttpClient(final HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }

    @Override
    public String fetch(@NonNull final HttpRequestContext requestContext) {
        try {
            log.info("Sending request: " + requestContext);
            HttpRequest httpRequest = buildHttpRequest(requestContext);
            final HttpResponse<String> httpResponse = java.net.http.HttpClient.newHttpClient()
                    .send(httpRequest, HttpResponse.BodyHandlers.ofString(requestContext.getCharset()));
            final int statusCode = httpResponse.statusCode();
            if (httpConfig.getMinSuccessStatus() <= statusCode && statusCode <= httpConfig.getMaxSuccessStatus()) {
                return httpResponse.body();
            } else {
                throw new IllegalStateException("Http request failed with status: " + statusCode);
            }
        } catch (final Exception e) {
            log.error("Failed to fetch resource from: " + requestContext.getUri() + " due to: " + e.getMessage(), e);
            throw new AbortTransformationException("Failed to fetch resource from: " + requestContext.getUri());
        }
    }

    private HttpRequest buildHttpRequest(@NotNull final HttpRequestContext requestContext) {
        final HttpRequest.Builder builder = requestContext.toHttpRequestBuilder();
        if (httpConfig.getTimeoutSeconds() > 0) {
            builder.timeout(Duration.ofSeconds(httpConfig.getTimeoutSeconds()));
        }
        if (httpConfig.isAddDefaultHeaders()) {
            addHeadersIfMissing(requestContext, builder);
        }
        return builder.build();
    }

    private void addHeadersIfMissing(@NotNull final HttpRequestContext requestContext,
                                     @NotNull final HttpRequest.Builder builder) {
        if (!requestContext.getHeaders().containsKey(USER_AGENT)) {
            builder.header(USER_AGENT, httpConfig.getUserAgent());
        }
        if (!requestContext.getHeaders().containsKey(ACCEPT)) {
            builder.header(ACCEPT, APPLICATION_JSON_VALUE);
        }
    }

    @Override
    public String fetch(@NonNull final HttpRequestContext baseContext,
                        @NonNull final HttpRequestContext overrides) {
        return this.fetch(baseContext.withOverrides(overrides));
    }
}
