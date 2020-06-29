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
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * {@link Function} allowing us to fetch HTTP resources with their URI provided by the JSON path.
 */
public final class HttpResourceContentFunction extends HttpResourceContentSupport<String> implements Function<String, String> {

    static final String NAME = "httpResourceByUri";
    private final Function<String, String> uriFunction;

    @NamedFunction(NAME)
    public HttpResourceContentFunction(@EmbedParam @Nullable final Map<String, RawConfigParam> uriFunction,
                                       @ValueParam @Nullable final String uri,
                                       @ValueParam @Nullable final String httpMethod,
                                       @MapParam @Nullable final Map<String, String> httpHeaders,
                                       @ValueParam @Nullable final String charset,
                                       @NonNull final FunctionRegistry functionRegistry,
                                       @NotNull final HttpClient httpClient) {
        super(httpClient, uri, httpMethod, httpHeaders, charset);
        this.uriFunction = Optional.ofNullable(uriFunction)
                .map(functionRegistry::<String, String>lookupFunction)
                .orElseGet(Function::identity);
    }

    @Override
    @NotNull
    protected HttpRequestContext buildOverrideContext(final String uriOverride) {
        return HttpRequestContext.builder()
                .uri(uriFunction.apply(uriOverride))
                .httpMethod((String) null)
                .charset(null)
                .build();
    }
}
