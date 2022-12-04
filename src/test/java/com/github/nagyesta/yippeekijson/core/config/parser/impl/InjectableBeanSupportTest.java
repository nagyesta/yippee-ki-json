package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.annotation.Injectable;
import com.github.nagyesta.yippeekijson.core.config.entities.HttpConfig;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.http.HttpClient;
import com.github.nagyesta.yippeekijson.core.http.impl.DefaultHttpClient;
import com.google.common.collect.ImmutableMap;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@LaunchAbortArmed
class InjectableBeanSupportTest {

    public static final String HTTP_CLIENT_1 = "httpClient1";
    public static final String HTTP_CLIENT_2 = "httpClient2";
    public static final String JSON_MAPPER = "jsonMapper";
    private static final DefaultHttpClient FIRST_HTTP_CLIENT_INSTANCE = new DefaultHttpClient(HttpConfig.builder().build());
    private static final DefaultHttpClient SECOND_HTTP_CLIENT_INSTANCE = new DefaultHttpClient(HttpConfig.builder().build());
    private static final JsonMapperImpl JSON_MAPPER_INSTANCE = new JsonMapperImpl();

    private static InjectableBeanSupport underTest;

    @SuppressWarnings("RedundantThrows")
    @BeforeAll
    static void beforeAll() throws Exception {
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBeansWithAnnotation(Injectable.class))
                .thenReturn(ImmutableMap.<String, Object>builder()
                        .put(HTTP_CLIENT_1, FIRST_HTTP_CLIENT_INSTANCE)
                        .put(HTTP_CLIENT_2, SECOND_HTTP_CLIENT_INSTANCE)
                        .put(JSON_MAPPER, JSON_MAPPER_INSTANCE)
                        .build());
        underTest = new InjectableBeanSupport(log) {
            @Override
            protected void afterInitialized() {
                //noop
            }
        };
        underTest.setApplicationContext(applicationContext);
        Assertions.assertDoesNotThrow(() -> underTest.afterPropertiesSet());
    }

    private static Stream<Arguments> validParameterProvider() throws NoSuchMethodException {
        return Stream.<Arguments>builder()
                .add(Arguments.of(constructor(JsonMapper.class, HttpClient.class, HttpClient.class), 0, JSON_MAPPER_INSTANCE))
                .add(Arguments.of(constructor(JsonMapper.class, HttpClient.class, HttpClient.class), 1, FIRST_HTTP_CLIENT_INSTANCE))
                .add(Arguments.of(constructor(JsonMapper.class, HttpClient.class, HttpClient.class), 2, SECOND_HTTP_CLIENT_INSTANCE))
                .add(Arguments.of(constructor(HttpClient.class, HttpClient.class), 0, SECOND_HTTP_CLIENT_INSTANCE))
                .add(Arguments.of(constructor(HttpClient.class, HttpClient.class), 1, SECOND_HTTP_CLIENT_INSTANCE))
                .add(Arguments.of(constructor(JsonMapper.class, HttpClient.class), 1, FIRST_HTTP_CLIENT_INSTANCE))
                .build();
    }

    @NotNull
    private static Constructor<ConstructorProvider> constructor(final Class<?>... params) throws NoSuchMethodException {
        return ConstructorProvider.class.getDeclaredConstructor(params);
    }

    @ParameterizedTest
    @MethodSource("validParameterProvider")
    void testValidCandidateOfShouldReturnExistingCandidates(final Constructor<ConstructorProvider> constructor,
                                                            final int parameterIndex,
                                                            final Object expected) {
        //given
        Parameter parameter = constructor.getParameters()[parameterIndex];

        //when
        final Object actual = underTest.validCandidateOf(constructor, parameter);

        //then
        Assertions.assertSame(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(classes = {JsonMapper.class, HttpClient.class})
    void testHasCandidateForShouldReturnTrueForExistingCandidates(final Class<?> clazz) {
        //given

        //when
        final boolean actual = underTest.hasCandidateFor(clazz);

        //then
        Assertions.assertTrue(actual);
    }

    @ParameterizedTest
    @ValueSource(classes = {JsonMapperImpl.class, DefaultHttpClient.class})
    void testHasCandidateForShouldReturnFalseForUnknownCandidates(final Class<?> clazz) {
        //given

        //when
        final boolean actual = underTest.hasCandidateFor(clazz);

        //then
        Assertions.assertFalse(actual);
    }

    @Test
    void testValidCandidateOfShouldThrowExceptionWhenCandidateNamesAreAmbiguous() throws NoSuchMethodException {
        //given
        final Constructor<ConstructorProvider> constructor = constructor(HttpClient.class);
        final Parameter parameter = constructor.getParameters()[0];

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.validCandidateOf(constructor, parameter));
    }

    @SuppressWarnings("unused")
    private static final class ConstructorProvider {

        private ConstructorProvider(final JsonMapper jsonMapper,
                                    @Named final HttpClient httpClient1,
                                    @Qualifier final HttpClient httpClient2) {
        }

        private ConstructorProvider(@Named("httpClient2") final HttpClient httpClient,
                                    @Qualifier final HttpClient httpClient2) {
        }

        private ConstructorProvider(final HttpClient httpClient) {
        }

        private ConstructorProvider(final JsonMapper jsonMapper, final HttpClient httpClient1) {
        }
    }
}
