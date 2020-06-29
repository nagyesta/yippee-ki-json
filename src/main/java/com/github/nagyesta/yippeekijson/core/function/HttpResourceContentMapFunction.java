package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.EmbedParam;
import com.github.nagyesta.yippeekijson.core.annotation.MapParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import com.github.nagyesta.yippeekijson.core.function.helper.HttpResourceContentSupport;
import com.github.nagyesta.yippeekijson.core.http.HttpClient;
import com.github.nagyesta.yippeekijson.core.http.HttpRequestContext;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * {@link Function} allowing us to fetch HTTP resources with their URI provided by the JSON path.
 */
public final class HttpResourceContentMapFunction extends HttpResourceContentSupport<Map<String, Object>>
        implements Function<Map<String, Object>, String> {

    static final String NAME = "httpResource";
    private final Function<Map<String, Object>, String> uriFunction;
    private final Function<Map<String, Object>, String> methodFunction;
    private final Function<Map<String, Object>, Map<String, String>> headerFunction;

    @SuppressWarnings("checkstyle:ParameterNumber")
    @NamedFunction(NAME)
    public HttpResourceContentMapFunction(@EmbedParam @Nullable final Map<String, RawConfigParam> uriFunction,
                                          @EmbedParam @Nullable final Map<String, RawConfigParam> methodFunction,
                                          @EmbedParam @Nullable final Map<String, RawConfigParam> headerFunction,
                                          @ValueParam @Nullable final String uri,
                                          @ValueParam @Nullable final String httpMethod,
                                          @MapParam @Nullable final Map<String, String> httpHeaders,
                                          @ValueParam @Nullable final String charset,
                                          @NonNull final FunctionRegistry functionRegistry,
                                          @NotNull final HttpClient httpClient) {
        super(httpClient, uri, httpMethod, httpHeaders, charset);
        this.uriFunction = Optional.ofNullable(uriFunction)
                .map(functionRegistry::<Map<String, Object>, String>lookupFunction)
                .orElseGet(() -> ignore -> null);
        this.methodFunction = Optional.ofNullable(methodFunction)
                .map(functionRegistry::<Map<String, Object>, String>lookupFunction)
                .orElseGet(() -> ignore -> null);
        this.headerFunction = Optional.ofNullable(headerFunction)
                .map(functionRegistry::<Map<String, Object>, Map<String, String>>lookupFunction)
                .orElseGet(() -> map -> Collections.emptyMap());
    }

    @Override
    @NotNull
    protected HttpRequestContext buildOverrideContext(final Map<String, Object> mapOverride) {
        return HttpRequestContext.builder()
                .uri(uriFunction.apply(mapOverride))
                .httpMethod(methodFunction.apply(mapOverride))
                .addHeaders(headerFunction.apply(mapOverride))
                .build();
    }
}
