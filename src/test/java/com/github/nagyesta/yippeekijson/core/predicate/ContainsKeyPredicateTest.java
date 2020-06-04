package com.github.nagyesta.yippeekijson.core.predicate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

class ContainsKeyPredicateTest {

    private static final String KEY = "key";
    private static final String KEY_1 = KEY + 1;
    private static final String KEY_2 = KEY + 2;
    private static final String VALUE = "value";

    private static Stream<Arguments> positiveMatchSupplier() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(KEY, Map.of(KEY, VALUE)))
                .add(Arguments.of(KEY_1, Map.of(KEY_1, VALUE)))
                .add(Arguments.of(KEY_2, Map.of(KEY_1, VALUE, KEY_2, VALUE)))
                .build();
    }

    private static Stream<Arguments> negativeMatchSupplier() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(KEY, null))
                .add(Arguments.of(KEY, List.of()))
                .add(Arguments.of(KEY, Collections.emptyMap()))
                .add(Arguments.of(KEY, Map.of(KEY_1, VALUE)))
                .add(Arguments.of(VALUE, Map.of(KEY_1, VALUE, KEY_2, VALUE)))
                .build();
    }

    @ParameterizedTest
    @MethodSource("positiveMatchSupplier")
    void testTestShouldMatchForPositiveCases(final String key, final Map<String, Object> map) {
        //given
        final ContainsKeyPredicate underTest = new ContainsKeyPredicate(key);

        //when
        final boolean actual = underTest.test(map);

        //then
        Assertions.assertTrue(actual);
    }

    @ParameterizedTest
    @MethodSource("negativeMatchSupplier")
    void testTestShouldNotMatchForNegativeCases(final String key, final Object map) {
        //given
        final ContainsKeyPredicate underTest = new ContainsKeyPredicate(key);

        //when
        final boolean actual = underTest.test(map);

        //then
        Assertions.assertFalse(actual);
    }

    @Test
    void testConstructorShouldFailForNull() {
        //given
        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ContainsKeyPredicate(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {KEY, KEY_1, VALUE})
    void testToStringShouldContainClassNameAndKey(final String key) {
        //given
        final ContainsKeyPredicate underTest = new ContainsKeyPredicate(key);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(ContainsKeyPredicate.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(key));
    }
}
