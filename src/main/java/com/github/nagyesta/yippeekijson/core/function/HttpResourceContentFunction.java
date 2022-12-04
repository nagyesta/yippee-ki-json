package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.EmbedParam;
import com.github.nagyesta.yippeekijson.core.annotation.MapParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import com.github.nagyesta.yippeekijson.core.function.helper.HttpResourceContentSupport;
import com.github.nagyesta.yippeekijson.core.http.HttpClient;
import com.github.nagyesta.yippeekijson.core.http.HttpMethod;
import com.github.nagyesta.yippeekijson.core.http.HttpRequestContext;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.*;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * {@link Function} allowing us to fetch HTTP resources with their URI provided by the JSON path.
 */
public final class HttpResourceContentFunction extends HttpResourceContentSupport<String> implements Function<String, String> {

    static final String NAME = "httpResourceByUri";
    static final String PARAM_URI_FUNCTION = "uriFunction";
    static final String PARAM_HTTP_HEADERS = "httpHeaders";
    private final Function<String, String> uriFunction;

    @SchemaDefinition(
            inputType = String.class,
            outputType = String.class,
            properties = @PropertyDefinitions(
                    value = {
                            @PropertyDefinition(name = PARAM_URI_FUNCTION,
                                    type = @TypeDefinition(itemType = Function.class, itemTypeParams = {String.class, String.class}),
                                    docs = "The function that is calculating the request URI based on the input map."
                            ),
                            @PropertyDefinition(name = PARAM_HTTP_HEADERS,
                                    type = @TypeDefinition(
                                            itemType = Map.class, itemTypeParams = {String.class, String.class}
                                    ),
                                    commonTypeRef = "#/definitions/commonTypes/definitions/httpHeaders"
                            )
                    },
                    minProperties = 1),
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_FUNCTIONS, section = "HTTP resource by URI function"),
            sinceVersion = WikiConstants.VERSION_1_2_0,
            description = {
                    "This function is providing a highly flexible HTTP download opportunity. It can operate in with only",
                    "providing the bare minimum of some base values (an URI, a HTTP method, some headers if needed) as",
                    "well as it offers the opportunity to use a function to find the URI based on the JSON/Supplier.",
                    "provided values."
            },
            example = @Example(
                    in = "/examples/json/http-download-input.json",
                    out = "/examples/json/http-download-function-output.json",
                    yml = "/examples/yml/http-download-function.yml",
                    skipTest = true,
                    note = "In this example we have used a supplier to provide input.")
    )
    @NamedFunction(NAME)
    public HttpResourceContentFunction(@EmbedParam @Nullable final Map<String, RawConfigParam> uriFunction,
                                       @ValueParam(docs = "The default request URI as a fallback.")
                                       @Nullable final String uri,
                                       @ValueParam(docs = "The default request method as a fallback. Defaults to GET.")
                                       @Nullable final HttpMethod httpMethod,
                                       @MapParam(docs = "The default request headers as a fallback.")
                                       @Nullable final Map<String, String> httpHeaders,
                                       @ValueParam(docs = "The charset used to read the response. Defaults to UTF-8.")
                                       @Nullable final Charset charset,
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
