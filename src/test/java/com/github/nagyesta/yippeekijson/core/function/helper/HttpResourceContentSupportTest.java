package com.github.nagyesta.yippeekijson.core.function.helper;

import com.github.nagyesta.yippeekijson.core.http.HttpClient;
import com.github.nagyesta.yippeekijson.core.http.HttpRequestContext;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.github.nagyesta.yippeekijson.core.http.HttpMethod.GET;
import static com.google.common.net.HttpHeaders.USER_AGENT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpResourceContentSupportTest {

    private static final String DEFAULT_URI = "http://localhost/default";
    private static final String OVERRIDE_URI = "http://localhost/override";
    private static final String SUCCESS = "{\"success\": true}";

    @Test
    void testApplyShouldExecuteStepsInOrderWhenCalledWithValidData() {
        //given
        final Map<String, String> headers = Map.of(USER_AGENT, USER_AGENT);
        final Charset charset = StandardCharsets.UTF_8;

        final HttpRequestContext overrideContext = HttpRequestContext.builder()
                .uri(OVERRIDE_URI)
                .build();

        HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.fetch(any(HttpRequestContext.class), same(overrideContext))).thenReturn(SUCCESS);
        HttpResourceContentSupport<Object> underTest = new HttpResourceContentSupport<>(
                httpClient, DEFAULT_URI, GET, headers, charset) {

            @Override
            protected @NotNull HttpRequestContext buildOverrideContext(final Object override) {
                Assertions.assertEquals(OVERRIDE_URI, override);
                return overrideContext;
            }
        };

        //when
        final String actual = underTest.apply(OVERRIDE_URI);

        //then
        Assertions.assertEquals(SUCCESS, actual);
    }

    @Test
    void testConstructorShouldThrowExceptionWhenHttpClientMissing() {
        //given

        //when + then exception
        //noinspection CodeBlock2Expr
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new HttpResourceContentSupport<>(null, null, null, null, null) {

                @Override
                protected @NotNull HttpRequestContext buildOverrideContext(final Object override) {
                    return HttpRequestContext.builder().build();
                }
            };
        });
    }

    @Test
    void testToStringShouldContainUriAndMethod() {
        //given
        HttpClient httpClient = mock(HttpClient.class);
        HttpResourceContentSupport<Object> underTest = new HttpResourceContentSupport<>(
                httpClient, DEFAULT_URI, null, null, null) {

            @Override
            protected @NotNull HttpRequestContext buildOverrideContext(final Object override) {
                return HttpRequestContext.builder().build();
            }
        };

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(DEFAULT_URI));
        Assertions.assertTrue(actual.contains(GET.name()));
    }
}
