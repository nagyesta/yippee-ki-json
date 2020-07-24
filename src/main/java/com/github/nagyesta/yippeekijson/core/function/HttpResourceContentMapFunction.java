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
import com.github.nagyesta.yippeekijson.metadata.schema.entity.typehelper.StringObjectMap;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.typehelper.StringStringMap;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.nio.charset.Charset;
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
    static final String PARAM_URI_FUNCTION = "uriFunction";
    static final String PARAM_METHOD_FUNCTION = "methodFunction";
    static final String PARAM_HEADER_FUNCTION = "headerFunction";
    static final String PARAM_HTTP_HEADERS = "httpHeaders";
    private final Function<Map<String, Object>, String> uriFunction;
    private final Function<Map<String, Object>, String> methodFunction;
    private final Function<Map<String, Object>, Map<String, String>> headerFunction;

    @SuppressWarnings("checkstyle:ParameterNumber")
    @SchemaDefinition(
            inputType = StringObjectMap.class,
            outputType = String.class,
            properties = @PropertyDefinitions(
                    value = {
                            @PropertyDefinition(name = PARAM_URI_FUNCTION,
                                    type = @TypeDefinition(
                                            itemType = Function.class, itemTypeParams = {StringObjectMap.class, String.class}),
                                    docs = "The function that is calculating the request URI based on the input map."
                            ),
                            @PropertyDefinition(name = PARAM_METHOD_FUNCTION,
                                    type = @TypeDefinition(
                                            itemType = Function.class, itemTypeParams = {StringObjectMap.class, String.class}),
                                    docs = "The function that is calculating the request method based on the input map."
                            ),
                            @PropertyDefinition(name = PARAM_HEADER_FUNCTION,
                                    type = @TypeDefinition(
                                            itemType = Function.class, itemTypeParams = {StringObjectMap.class, StringStringMap.class}),
                                    docs = "The function that is calculating the request headers based on the input map."
                            ),
                            @PropertyDefinition(name = PARAM_HTTP_HEADERS,
                                    type = @TypeDefinition(
                                            itemType = Map.class, itemTypeParams = {String.class, String.class}
                                    ),
                                    commonTypeRef = "#/definitions/commonTypes/definitions/httpHeaders"
                            )
                    },
                    minProperties = 1
            ),
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_FUNCTIONS, section = "HTTP resource content map function"),
            sinceVersion = WikiConstants.VERSION_1_2_0,
            description = {
                    "This function allows fully dynamic calculation of all component of the HTTP request while it let's us",
                    "define fallback values in case overrides are not possible or not desired for any aspects.",
                    "",
                    "This function offers more flexibility than what the rest of the components could fully utilize. Please",
                    "expect more updates unleashing the full potential of this component."
            }
    )
    @NamedFunction(NAME)
    public HttpResourceContentMapFunction(@EmbedParam @Nullable final Map<String, RawConfigParam> uriFunction,
                                          @EmbedParam @Nullable final Map<String, RawConfigParam> methodFunction,
                                          @EmbedParam @Nullable final Map<String, RawConfigParam> headerFunction,
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
