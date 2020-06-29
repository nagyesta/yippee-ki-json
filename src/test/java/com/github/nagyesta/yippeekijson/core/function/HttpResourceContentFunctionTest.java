package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.params.RawConfigValue;
import com.github.nagyesta.yippeekijson.core.http.HttpClient;
import com.github.nagyesta.yippeekijson.core.http.HttpRequestContext;
import com.google.common.base.Functions;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpResourceContentFunctionTest {

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
        Assertions.assertThrows(IllegalArgumentException.class, () -> new HttpResourceContentFunction(
                null,
                null, null, null, null,
                functionRegistry, httpClient));
    }

    @Test
    void testBuildOverrideContextShouldUseTheFunctionsToTransformInputNoDefaults() {
        //given
        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        HttpClient httpClient = mock(HttpClient.class);
        final Map<String, RawConfigParam> uriMap = this.prepareFunctionMockCall(functionRegistry);

        HttpResourceContentFunction underTest = new HttpResourceContentFunction(
                uriMap,
                null, null, Map.of(NAME, HEADER), null,
                functionRegistry, httpClient);

        //when
        final HttpRequestContext actual = underTest.buildOverrideContext(URI);

        //then
        assertValid(actual);
    }

    @Test
    void testBuildOverrideContextShouldUseTheFunctionsToTransformInput() {
        //given
        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        HttpClient httpClient = mock(HttpClient.class);
        final Map<String, RawConfigParam> uriMap = this.prepareFunctionMockCall(functionRegistry);

        HttpResourceContentFunction underTest = new HttpResourceContentFunction(
                uriMap,
                NAME, HttpRequestContext.HttpMethod.GET.name(), Map.of(NAME, HEADER), StandardCharsets.UTF_8.name(),
                functionRegistry, httpClient);

        //when
        final HttpRequestContext actual = underTest.buildOverrideContext(URI);

        //then
        assertValid(actual);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testApplyShouldFallbackWhenTransformFunctionsMissing() {
        //given
        FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
        HttpClient httpClient = mock(HttpClient.class);

        HttpResourceContentFunction underTest = new HttpResourceContentFunction(
                null,
                URI, HttpRequestContext.HttpMethod.POST.name(), Map.of(NAME, HEADER), StandardCharsets.UTF_8.name(),
                functionRegistry, httpClient);

        final HttpRequestContext baseContext = HttpRequestContext.builder()
                .uri(URI)
                .httpMethod(HttpRequestContext.HttpMethod.POST)
                .addHeader(NAME, HEADER)
                .charset(StandardCharsets.UTF_8)
                .build();
        final HttpRequestContext emptyOverrides = HttpRequestContext.builder()
                .httpMethod((HttpRequestContext.HttpMethod) null)
                .uri(null)
                .charset(null)
                .build();
        when(httpClient.fetch(eq(baseContext), eq(emptyOverrides))).thenReturn(JSON);

        //when
        final String actual = underTest.apply(null);

        //then
        Assertions.assertEquals(JSON, actual);
    }

    @NotNull
    private Map<String, RawConfigParam> prepareFunctionMockCall(final FunctionRegistry functionRegistry) {
        final Map<String, RawConfigParam> paramMap = Map.of(NAME, new RawConfigValue(NAME, HttpResourceContentFunctionTest.URI));
        when(functionRegistry.<String, String>lookupFunction(eq(paramMap))).thenReturn(Functions.identity());
        return paramMap;
    }

    private void assertValid(final HttpRequestContext actual) {
        Assertions.assertEquals(URI, actual.getUri());
        Assertions.assertNull(actual.getHttpMethod());
        Assertions.assertEquals(Map.<String, List<String>>of(), actual.getHeaders());
        Assertions.assertNull(actual.getCharset());
    }
}
