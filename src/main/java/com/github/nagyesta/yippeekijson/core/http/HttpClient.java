package com.github.nagyesta.yippeekijson.core.http;

import com.github.nagyesta.yippeekijson.core.exception.AbortTransformationException;
import lombok.NonNull;

/**
 * Defines how the HTTP client abstraction behaves.
 */
public interface HttpClient {

    /**
     * Fetches a resource over HTTP based on the {@link HttpRequestContext}.
     *
     * @param requestContext The context defining how the resource should be accessed
     * @return The {@link String} contents of the resource
     * @throws AbortTransformationException If the fetching fails for any reason.
     */
    String fetch(@NonNull HttpRequestContext requestContext) throws AbortTransformationException;

    /**
     * Fetches a resource over HTTP based on the merger {@link HttpRequestContext}.
     * During the merge, if a value is present in the overrides, it will be used from the overrides.
     * If an HTTP header is present on both sides, the value in the baseContext will be ignored.
     *
     * @param baseContext The base context providing the defaults
     * @param overrides   The override context providing values to be applied on top of the defaults
     * @return The {@link String} contents of the resource
     * @throws AbortTransformationException If the fetching fails for any reason.
     */
    String fetch(@NonNull HttpRequestContext baseContext,
                 @NonNull HttpRequestContext overrides) throws AbortTransformationException;
}
