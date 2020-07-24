package com.github.nagyesta.yippeekijson.core.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.stream.Stream;

class HttpRequestContextTest {

    private static final HttpRequestContext EMPTY = HttpRequestContext.builder().build();
    private static final String URI = "URI";

    private static HttpRequestContext buildPost() {
        return HttpRequestContext.builder()
                .uri(URI)
                .httpMethod(HttpMethod.POST)
                .build();
    }

    private static Stream<Arguments> pairSupplier() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(EMPTY, null, false))
                .add(Arguments.of(EMPTY, EMPTY, true))
                .add(Arguments.of(EMPTY, new Object(), false))
                .add(Arguments.of(buildPost(), buildPost(), true))
                .add(Arguments.of(buildPost(), EMPTY, false))
                .build();
    }

    @ParameterizedTest
    @MethodSource("pairSupplier")
    void testEquals(final Object underTest, final Object other, final boolean expected) {
        //given

        //when
        final boolean actual = underTest.equals(other);

        //then
        Assertions.assertEquals(expected, actual);
    }


    @ParameterizedTest
    @MethodSource("pairSupplier")
    void testHashCode(final Object underTest, final Object other, final boolean expected) {
        //given

        //when
        final int actualHash = Objects.hash(underTest);
        final int otherHash = Objects.hash(other);

        //then
        if (expected) {
            Assertions.assertEquals(otherHash, actualHash);
        } else {
            Assertions.assertNotEquals(otherHash, actualHash);
        }
    }
}
