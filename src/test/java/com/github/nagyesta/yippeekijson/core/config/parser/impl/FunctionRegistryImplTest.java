package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.annotation.MethodParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.function.AnyStringPredicate;
import com.github.nagyesta.yippeekijson.core.function.RegexReplaceFunction;
import com.github.nagyesta.yippeekijson.core.function.StaticStringSupplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

class FunctionRegistryImplTest {

    private static final String FAILING = "failing";
    private static final String NONE = "none";
    private static final String UNKNOWN = "unknown";
    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final String BLANK = " ";
    private static final String STATIC_STRING = "staticString";
    private static final String REGEX = "regex";
    private static final String PATTERN = "pattern";
    private static final String REPLACEMENT = "replacement";
    private static final String ANY_STRING = "anyString";

    private static Stream<Arguments> invalidMapProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(Map.of(NAME, UNKNOWN, VALUE, VALUE), IllegalArgumentException.class))
                .add(Arguments.of(Map.of(NAME, UNKNOWN), IllegalArgumentException.class))
                .add(Arguments.of(Map.of(NAME, FAILING), IllegalArgumentException.class))
                .add(Arguments.of(Map.of(NAME, FAILING, NONE, BLANK), IllegalStateException.class))
                .add(Arguments.of(Collections.emptyMap(), IllegalArgumentException.class))
                .add(Arguments.of(null, IllegalArgumentException.class))
                .build();
    }

    private static Stream<Arguments> nullListProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null, null))
                .add(Arguments.of(Collections.emptyList(), null, null))
                .add(Arguments.of(null, Collections.emptyList(), null))
                .add(Arguments.of(null, null, Collections.emptyList()))
                .add(Arguments.of(Collections.emptyList(), Collections.emptyList(), null))
                .add(Arguments.of(Collections.emptyList(), null, Collections.emptyList()))
                .add(Arguments.of(null, Collections.emptyList(), Collections.emptyList()))
                .build();
    }

    @ParameterizedTest
    @MethodSource("nullListProvider")
    void testConstructorShouldFailIfNullProvided(List<Class<? extends Supplier<?>>> suppliers,
                                                 List<Class<? extends Function<?, ?>>> functions,
                                                 List<Class<? extends Predicate<?>>> predicates) {
        //given

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> new FunctionRegistryImpl(suppliers, functions, predicates));
    }

    @Test
    void testLookupSupplierShouldReturnAnInstanceWhenFound() {
        //given
        FunctionRegistry underTest = new FunctionRegistryImpl(List.of(StaticStringSupplier.class),
                Collections.emptyList(), Collections.emptyList());

        //when
        final Supplier<Object> actual = underTest.lookupSupplier(Map.of(NAME, STATIC_STRING, VALUE, VALUE));

        //then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(StaticStringSupplier.class, actual.getClass());
    }

    @ParameterizedTest
    @MethodSource("invalidMapProvider")
    void testLookupSupplierShouldThrowExceptionWhenSupplierNotFound(Map<String, String> map, Class<? extends Exception> exception) {
        //given
        FunctionRegistry underTest = new FunctionRegistryImpl(Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList());
        underTest.registerSupplierClass(FailingSupplier.class);

        //when + then exception
        Assertions.assertThrows(exception, () -> underTest.lookupSupplier(map));
    }

    @Test
    void testLookupFunctionShouldReturnAnInstanceWhenFound() {
        //given
        FunctionRegistry underTest = new FunctionRegistryImpl(Collections.emptyList(),
                List.of(RegexReplaceFunction.class), Collections.emptyList());

        //when
        final Function<Object, Object> actual = underTest.lookupFunction(
                Map.of(NAME, REGEX, PATTERN, VALUE, REPLACEMENT, NONE));

        //then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(RegexReplaceFunction.class, actual.getClass());
    }

    @ParameterizedTest
    @MethodSource("invalidMapProvider")
    void testLookupFunctionShouldThrowExceptionWhenFunctionNotFound(Map<String, String> map, Class<? extends Exception> exception) {
        //given
        FunctionRegistry underTest = new FunctionRegistryImpl(Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList());
        underTest.registerFunctionClass(FailingFunction.class);

        //when + then exception
        Assertions.assertThrows(exception, () -> underTest.lookupFunction(map));
    }

    @Test
    void testLookupPredicateShouldReturnAnInstanceWhenFound() {
        //given
        FunctionRegistry underTest = new FunctionRegistryImpl(Collections.emptyList(),
                Collections.emptyList(), List.of(AnyStringPredicate.class));

        //when
        final Predicate<Object> actual = underTest.lookupPredicate(
                Map.of(NAME, ANY_STRING));

        //then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(AnyStringPredicate.class, actual.getClass());
    }

    @ParameterizedTest
    @MethodSource("invalidMapProvider")
    void testLookupPredicateShouldThrowExceptionWhenPredicateNotFound(Map<String, String> map, Class<? extends Exception> exception) {
        //given
        FunctionRegistry underTest = new FunctionRegistryImpl(Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList());
        underTest.registerPredicateClass(FailingPredicate.class);

        //when + then exception
        Assertions.assertThrows(exception, () -> underTest.lookupPredicate(map));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(classes = {WrongSupplier.class, FailingSupplier.class})
    void testRegisterSupplierClassShouldThrowExceptionForInvalidInput(Class<? extends Supplier<?>> clazz) {
        //given
        FunctionRegistry underTest = new FunctionRegistryImpl(Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList());
        underTest.registerSupplierClass(FailingSupplier.class);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.registerSupplierClass(clazz));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(classes = {WrongFunction.class, FailingFunction.class})
    void testRegisterFunctionClassShouldThrowExceptionForInvalidInput(Class<? extends Function<?, ?>> clazz) {
        //given
        FunctionRegistry underTest = new FunctionRegistryImpl(Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList());
        underTest.registerFunctionClass(FailingFunction.class);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.registerFunctionClass(clazz));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(classes = {WrongPredicate.class, FailingPredicate.class})
    void testPredicateFunctionClassShouldThrowExceptionForInvalidInput(Class<? extends Predicate<?>> clazz) {
        //given
        FunctionRegistry underTest = new FunctionRegistryImpl(Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList());
        underTest.registerPredicateClass(FailingPredicate.class);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.registerPredicateClass(clazz));
    }

    private static class WrongSupplier implements Supplier<String> {
        @Override
        public String get() {
            return null;
        }
    }

    private static class FailingSupplier extends WrongSupplier {
        @NamedSupplier(FAILING)
        private FailingSupplier(@MethodParam(NONE) String something) {
            if (something.isBlank()) {
                throw new RuntimeException(something);
            }
        }
    }

    private static class WrongFunction implements Function<String, String> {
        @Override
        public String apply(String s) {
            return null;
        }
    }

    private static class FailingFunction extends WrongFunction {
        @NamedFunction(FAILING)
        private FailingFunction(@MethodParam(NONE) String something) {
            if (something.isBlank()) {
                throw new RuntimeException(something);
            }
        }
    }

    private static class WrongPredicate implements Predicate<String> {
        @Override
        public boolean test(String s) {
            return false;
        }
    }

    private static class FailingPredicate extends WrongPredicate {
        @NamedPredicate(FAILING)
        private FailingPredicate(@MethodParam(NONE) String something) {
            if (something.isBlank()) {
                throw new RuntimeException(something);
            }
        }
    }
}
