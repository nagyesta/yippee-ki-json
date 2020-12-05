package com.github.nagyesta.yippeekijson.core.http.impl;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.config.entities.HttpConfig;
import com.github.nagyesta.yippeekijson.core.exception.AbortTransformationException;
import com.github.nagyesta.yippeekijson.core.http.HttpClient;
import com.github.nagyesta.yippeekijson.core.http.HttpRequestContext;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.google.common.net.HttpHeaders.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Slf4j
@LaunchAbortArmed
class DefaultHttpClientTest {

    private static final String SUCCESS = "success";
    private static final Map<String, Boolean> JSON_MAP = Map.of(SUCCESS, Boolean.TRUE);
    private static final String SUCCESS_TRUE = Json.write(JSON_MAP);
    private static final String USER_AGENT_VALUE = "user agent";
    private static final int SUCCESS_STATUS_MIN = 200;
    private static final int SUCCESS_STATUS_MAX = 299;
    private static final int TIMEOUT_SECONDS = 5;
    private static final int TIMEOUT_OFF = 0;
    private static final String SUCCESS_JSON = "/success.json";
    private static final String FAILURE_JSON = "/failure.json";
    private static final String GET = "GET";
    private static final String POST = "POST";

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void beforeAll() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();

        final StubMapping getMapping = WireMock.get(SUCCESS_JSON)
                .withHeader(USER_AGENT, new EqualToPattern(USER_AGENT_VALUE))
                .withHeader(ACCEPT, new EqualToPattern(APPLICATION_JSON_VALUE))
                .withHeader(ACCEPT_CHARSET, new EqualToPattern(StandardCharsets.UTF_8.name()))
                .willReturn(ResponseDefinitionBuilder.okForJson(JSON_MAP))
                .build();
        wireMockServer.addStubMapping(getMapping);

        final StubMapping postMapping = WireMock.post(SUCCESS_JSON)
                .withHeader(USER_AGENT, new EqualToPattern(USER_AGENT_VALUE))
                .withHeader(ACCEPT, new EqualToPattern(APPLICATION_JSON_VALUE))
                .withHeader(ACCEPT_CHARSET, new EqualToPattern(StandardCharsets.UTF_8.name()))
                .willReturn(ResponseDefinitionBuilder.okForJson(JSON_MAP))
                .build();
        wireMockServer.addStubMapping(postMapping);
    }

    @AfterAll
    static void afterAll() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    private static Stream<Arguments> nullSource() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(HttpRequestContext.builder().build(), null))
                .add(Arguments.of(null, HttpRequestContext.builder().build()))
                .build();
    }

    @ParameterizedTest
    @ValueSource(strings = {GET, POST})
    void testFetchShouldSendRequestAndProcessSuccessResponse(final String method) {
        //given
        String baseUrl = wireMockServer.baseUrl();

        final HttpRequestContext requestContext = HttpRequestContext.builder()
                .httpMethod(method)
                .uri(baseUrl + SUCCESS_JSON)
                .build();

        HttpConfig config = httpConfig(true, TIMEOUT_OFF);
        HttpClient underTest = new DefaultHttpClient(config);

        //when
        final String actual = underTest.fetch(requestContext);

        //then
        Assertions.assertEquals(SUCCESS_TRUE, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {GET, POST})
    void testFetchShouldSendRequestAndThrowExceptionForWrongStatusCode(final String method) {
        //given
        String baseUrl = wireMockServer.baseUrl();

        final HttpRequestContext requestContext = HttpRequestContext.builder()
                .httpMethod(method)
                .uri(baseUrl + FAILURE_JSON)
                .build();

        HttpConfig config = httpConfig(false, TIMEOUT_SECONDS);
        HttpClient underTest = new DefaultHttpClient(config);

        //when + then exception
        Assertions.assertThrows(AbortTransformationException.class, () -> underTest.fetch(requestContext));
    }

    @ParameterizedTest
    @ValueSource(strings = {GET, POST})
    void testFetchMergeShouldSendRequestAndProcessSuccessResponse(final String method) {
        //given
        String baseUrl = wireMockServer.baseUrl();

        final HttpRequestContext requestContext = HttpRequestContext.builder()
                .httpMethod(method)
                .uri(baseUrl + FAILURE_JSON)
                .addHeaders(Map.of(USER_AGENT, USER_AGENT))
                .addHeader(ACCEPT_CHARSET, StandardCharsets.UTF_8.name())
                .build();

        final HttpRequestContext overrideContext = HttpRequestContext.builder()
                .uri(baseUrl + SUCCESS_JSON)
                .addHeader(USER_AGENT, USER_AGENT_VALUE)
                .addHeader(ACCEPT, APPLICATION_JSON_VALUE)
                .build();

        HttpConfig config = httpConfig(false, TIMEOUT_SECONDS);
        HttpClient underTest = new DefaultHttpClient(config);

        //when
        final String actual = underTest.fetch(requestContext, overrideContext);

        //then
        Assertions.assertEquals(SUCCESS_TRUE, actual);
    }

    @Test
    void testFetchShouldThrowExceptionForNull() {
        //given

        HttpConfig config = httpConfig(false, TIMEOUT_SECONDS);
        HttpClient underTest = new DefaultHttpClient(config);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.fetch(null));
    }

    @ParameterizedTest
    @MethodSource("nullSource")
    void testFetchShouldThrowExceptionForNulls(final HttpRequestContext base, final HttpRequestContext override) {
        //given
        HttpConfig config = httpConfig(false, TIMEOUT_SECONDS);
        HttpClient underTest = new DefaultHttpClient(config);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.fetch(base, override));
    }

    private HttpConfig httpConfig(final boolean addDefaultHeaders, final int timeout) {
        return HttpConfig.builder()
                .addDefaultHeaders(addDefaultHeaders)
                .maxSuccessStatus(SUCCESS_STATUS_MAX)
                .minSuccessStatus(SUCCESS_STATUS_MIN)
                .timeoutSeconds(timeout)
                .userAgent(USER_AGENT_VALUE)
                .build();
    }
}
