package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.http.HttpClient;
import com.github.nagyesta.yippeekijson.core.http.HttpRequestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HttpResourceContentSupplierTest {
    private static final String VALIDATION_INPUT_JSON = "http://localhost/validation/validation-input.json";
    private static final String EXAMPLE_JSON = "http://localhost/json/example.json";

    private static Stream<Arguments> nullProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null))
                .add(Arguments.of(EXAMPLE_JSON, null))
                .add(Arguments.of(null, mock(HttpClient.class)))
                .build();
    }

    @ParameterizedTest
    @ValueSource(strings = {EXAMPLE_JSON, VALIDATION_INPUT_JSON})
    void testGetShouldReturnTheStaticString(final String uri) {
        //given
        final HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.fetch(any(HttpRequestContext.class))).thenReturn(uri);
        final HttpResourceContentSupplier underTest = new HttpResourceContentSupplier(
                uri, null, null, StandardCharsets.UTF_8.name(), httpClient);

        //when
        final String actual = underTest.get();

        //then
        Assertions.assertEquals(uri, actual);
        verify(httpClient).fetch(argThat(argument -> argument.getUri().equals(uri)));
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testConstructorShouldNotAllowNulls(final String uri, final HttpClient httpClient) {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new HttpResourceContentSupplier(uri, null, null, StandardCharsets.UTF_8.name(), httpClient));
    }

    @ParameterizedTest
    @ValueSource(strings = {VALIDATION_INPUT_JSON, EXAMPLE_JSON})
    void testToStringShouldContainClassNameAndKey(final String uri) {
        //given
        final HttpResourceContentSupplier underTest = new HttpResourceContentSupplier(
                uri, null, null, StandardCharsets.UTF_8.name(), mock(HttpClient.class));

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(HttpResourceContentSupplier.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(uri));
        Assertions.assertTrue(actual.contains(StandardCharsets.UTF_8.name()));
    }
}
