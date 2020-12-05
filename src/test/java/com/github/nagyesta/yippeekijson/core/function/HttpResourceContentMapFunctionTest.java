package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.params.RawConfigValue;
import com.github.nagyesta.yippeekijson.core.http.HttpClient;
import com.github.nagyesta.yippeekijson.core.http.HttpMethod;
import com.github.nagyesta.yippeekijson.core.http.HttpRequestContext;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.common.net.HttpHeaders.ACCEPT_CHARSET;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@LaunchAbortArmed
class HttpResourceContentMapFunctionTest {

    private static final String JSON = "{}";
    private static final String NAME = "name";
    private static final String URI = "uri";
    private static final String METHOD = "method";
    private static final String HEADER = "header";

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(mock(FunctionRegistry.class), null))
                .add(Arguments.of(null, mock(HttpClient.class)))
                .build();
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldThrowExceptionIfNullsProvidedForMandatoryParams(final FunctionRegistry functionRegistry,
                                                                              final HttpClient httpClient) {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new HttpResourceContentMapFunction(
                null, null, null,
                null, null, null, null,
                functionRegistry, httpClient));
    }

    @Test
    void testBuildOverrideContextShouldUseTheFunctionsToTransformInputNoDefaults() {
        //given
        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        HttpClient httpClient = mock(HttpClient.class);
        final Map<String, RawConfigParam> uriMap = this.<String>prepareFunctionMockCall(functionRegistry, URI);
        final Map<String, RawConfigParam> methodMap = this.<String>prepareFunctionMockCall(functionRegistry, METHOD);
        final Map<String, RawConfigParam> headerMap = this.<Map<String, String>>prepareFunctionMockCall(functionRegistry, HEADER);

        HttpResourceContentMapFunction underTest = new HttpResourceContentMapFunction(
                uriMap, methodMap, headerMap,
                null, null, null, null,
                functionRegistry, httpClient);

        final Map<String, Object> mapOverride = Map.of(
                URI, URI,
                METHOD, HttpMethod.POST.name(),
                HEADER, Map.of(NAME, HEADER));

        //when
        final HttpRequestContext actual = underTest.buildOverrideContext(mapOverride);

        //then
        assertValid(actual);
    }

    @Test
    void testBuildOverrideContextShouldUseTheFunctionsToTransformInput() {
        //given
        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        HttpClient httpClient = mock(HttpClient.class);
        final Map<String, RawConfigParam> uriMap = this.<String>prepareFunctionMockCall(functionRegistry, URI);
        final Map<String, RawConfigParam> methodMap = this.<String>prepareFunctionMockCall(functionRegistry, METHOD);
        final Map<String, RawConfigParam> headerMap = this.<Map<String, String>>prepareFunctionMockCall(functionRegistry, HEADER);

        HttpResourceContentMapFunction underTest = new HttpResourceContentMapFunction(
                uriMap, methodMap, headerMap,
                NAME, HttpMethod.GET, null, StandardCharsets.UTF_8,
                functionRegistry, httpClient);

        final Map<String, Object> mapOverride = Map.of(
                URI, URI,
                METHOD, HttpMethod.POST.name(),
                HEADER, Map.of(NAME, HEADER));

        //when
        final HttpRequestContext actual = underTest.buildOverrideContext(mapOverride);

        //then
        assertValid(actual);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testApplyShouldFallbackWhenTransformFunctionsMissing() {
        //given
        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        HttpClient httpClient = mock(HttpClient.class);

        HttpResourceContentMapFunction underTest = new HttpResourceContentMapFunction(
                null, null, null,
                URI, HttpMethod.POST, Map.of(NAME, HEADER), StandardCharsets.UTF_8,
                functionRegistry, httpClient);

        final Map<String, Object> mapOverride = Map.of();
        final HttpRequestContext baseContext = HttpRequestContext.builder()
                .uri(URI)
                .httpMethod(HttpMethod.POST)
                .addHeader(NAME, HEADER)
                .charset(StandardCharsets.UTF_8)
                .build();
        final HttpRequestContext emptyOverrides = HttpRequestContext.builder()
                .httpMethod((HttpMethod) null)
                .build();
        when(httpClient.fetch(eq(baseContext), eq(emptyOverrides))).thenReturn(JSON);

        //when
        final String actual = underTest.apply(mapOverride);

        //then
        Assertions.assertEquals(JSON, actual);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private <T> Map<String, RawConfigParam> prepareFunctionMockCall(final FunctionRegistry functionRegistry, final String value) {
        final Map<String, RawConfigParam> paramMap = Map.of(NAME, new RawConfigValue(NAME, value));
        when(functionRegistry.<Map<String, Object>, T>lookupFunction(eq(paramMap)))
                .thenReturn(map -> (T) map.get(value));
        return paramMap;
    }

    private void assertValid(final HttpRequestContext actual) {
        Assertions.assertEquals(URI, actual.getUri());
        Assertions.assertEquals(HttpMethod.POST, actual.getHttpMethod());
        Assertions.assertEquals(Map.of(ACCEPT_CHARSET, List.of(StandardCharsets.UTF_8.name()), NAME, List.of(HEADER)), actual.getHeaders());
        Assertions.assertEquals(StandardCharsets.UTF_8, actual.getCharset());
    }
}
