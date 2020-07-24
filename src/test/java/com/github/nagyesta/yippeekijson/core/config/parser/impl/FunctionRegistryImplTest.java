package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.annotation.*;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.params.RawConfigValue;
import com.github.nagyesta.yippeekijson.core.function.RegexReplaceFunction;
import com.github.nagyesta.yippeekijson.core.predicate.AnyStringPredicate;
import com.github.nagyesta.yippeekijson.core.supplier.StaticStringSupplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FunctionRegistryImplTest {

    private static final String FAILING = "failing";
    private static final String NONE = "none";
    private static final String UNKNOWN = "unknown";
    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final String BLANK = " ";
    private static final RawConfigParam STATIC_STRING = new RawConfigValue(NAME, "staticString");
    private static final RawConfigParam REGEX = new RawConfigValue(NAME, "regex");
    private static final String PATTERN = "pattern";
    private static final String REPLACEMENT = "replacement";
    private static final RawConfigParam ANY_STRING = new RawConfigValue(NAME, "anyString");

    private static Stream<Arguments> invalidMapProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(Map.of(NAME, new RawConfigValue(NAME, UNKNOWN), VALUE, new RawConfigValue(NAME, VALUE)),
                        IllegalArgumentException.class))
                .add(Arguments.of(Map.of(NAME, new RawConfigValue(NAME, UNKNOWN)), IllegalArgumentException.class))
                .add(Arguments.of(Map.of(NAME, new RawConfigValue(NAME, FAILING)), IllegalArgumentException.class))
                .add(Arguments.of(Map.of(NAME, new RawConfigValue(NAME, FAILING), NONE, new RawConfigValue(NAME, BLANK)),
                        IllegalStateException.class))
                .add(Arguments.of(Map.of(NAME, new RawConfigValue(NAME, FAILING), NONE, new RawConfigValue(NAME, BLANK),
                        UNKNOWN, new RawConfigValue(NAME, UNKNOWN)), IllegalStateException.class))
                .add(Arguments.of(Collections.emptyMap(), IllegalArgumentException.class))
                .add(Arguments.of(null, IllegalArgumentException.class))
                .build();
    }

    private static Stream<Arguments> nullListProvider() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, null, null, null))
                .add(Arguments.of(Collections.emptyList(), null, null, null))
                .add(Arguments.of(null, Collections.emptyList(), null, null))
                .add(Arguments.of(null, null, Collections.emptyList(), null))
                .add(Arguments.of(null, null, null, mock(ConversionService.class)))
                .add(Arguments.of(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), null))
                .add(Arguments.of(Collections.emptyList(), Collections.emptyList(), null, mock(ConversionService.class)))
                .add(Arguments.of(Collections.emptyList(), null, Collections.emptyList(), mock(ConversionService.class)))
                .add(Arguments.of(null, Collections.emptyList(), Collections.emptyList(), mock(ConversionService.class)))
                .build();
    }

    @ParameterizedTest
    @MethodSource("nullListProvider")
    void testConstructorShouldFailIfNullProvided(final List<Class<? extends Supplier<?>>> suppliers,
                                                 final List<Class<? extends Function<?, ?>>> functions,
                                                 final List<Class<? extends Predicate<Object>>> predicates,
                                                 final ConversionService conversionService) {
        //given
        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new FunctionRegistryImpl(suppliers, functions, predicates, conversionService));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testLookupSupplierShouldReturnAnInstanceWhenFound() throws Exception {
        //given
        final ConversionService conversionService = mock(ConversionService.class);
        when(conversionService.convert(any(), any(Class.class))).then(a -> a.getArgument(0));
        final FunctionRegistry underTest = new FunctionRegistryImpl(List.of(StaticStringSupplier.class),
                Collections.emptyList(), Collections.emptyList(), conversionService);
        final ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBeansWithAnnotation(Injectable.class)).thenReturn(Map.of());
        underTest.setApplicationContext(applicationContext);
        underTest.afterPropertiesSet();

        //when
        final Supplier<Object> actual = underTest.lookupSupplier(Map.of(NAME, STATIC_STRING, VALUE, new RawConfigValue(NAME, VALUE)));

        //then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(StaticStringSupplier.class, actual.getClass());
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @MethodSource("invalidMapProvider")
    void testLookupSupplierShouldThrowExceptionWhenSupplierNotFound(final Map<String, RawConfigParam> map,
                                                                    final Class<? extends Exception> exception) throws Exception {
        //given
        final ConversionService conversionService = mock(ConversionService.class);
        when(conversionService.convert(any(), any(Class.class))).thenAnswer(a -> a.getArgument(0));
        final FunctionRegistry underTest = new FunctionRegistryImpl(Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), conversionService);
        final ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBeansWithAnnotation(Injectable.class)).thenReturn(Map.of());
        underTest.setApplicationContext(applicationContext);
        underTest.afterPropertiesSet();
        underTest.registerSupplierClass(FailingSupplier.class);

        //when + then exception
        Assertions.assertThrows(exception, () -> underTest.lookupSupplier(map));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testLookupFunctionShouldReturnAnInstanceWhenFound() throws Exception {
        //given
        final ConversionService conversionService = mock(ConversionService.class);
        when(conversionService.convert(any(), any(Class.class))).then(a -> a.getArgument(0));
        final FunctionRegistry underTest = new FunctionRegistryImpl(Collections.emptyList(),
                List.of(RegexReplaceFunction.class), Collections.emptyList(), conversionService);
        final ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBeansWithAnnotation(Injectable.class)).thenReturn(Map.of());
        underTest.setApplicationContext(applicationContext);
        underTest.afterPropertiesSet();

        //when
        final Function<Object, Object> actual = underTest.lookupFunction(
                Map.of(NAME, REGEX, PATTERN, new RawConfigValue(NAME, VALUE), REPLACEMENT, new RawConfigValue(NAME, NONE)));

        //then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(RegexReplaceFunction.class, actual.getClass());
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @MethodSource("invalidMapProvider")
    void testLookupFunctionShouldThrowExceptionWhenFunctionNotFound(final Map<String, RawConfigParam> map,
                                                                    final Class<? extends Exception> exception) throws Exception {
        //given
        final ConversionService conversionService = mock(ConversionService.class);
        when(conversionService.convert(any(), any(Class.class))).thenAnswer(a -> a.getArgument(0));
        final FunctionRegistry underTest = new FunctionRegistryImpl(Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), conversionService);
        final ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBeansWithAnnotation(Injectable.class)).thenReturn(Map.of());
        underTest.setApplicationContext(applicationContext);
        underTest.afterPropertiesSet();
        underTest.registerFunctionClass(FailingFunction.class);

        //when + then exception
        Assertions.assertThrows(exception, () -> underTest.lookupFunction(map));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testLookupPredicateShouldReturnAnInstanceWhenFound() throws Exception {
        //given
        final ConversionService conversionService = mock(ConversionService.class);
        when(conversionService.convert(any(), any(Class.class))).thenAnswer(a -> a.getArgument(0));
        final FunctionRegistry underTest = new FunctionRegistryImpl(Collections.emptyList(),
                Collections.emptyList(), List.of(AnyStringPredicate.class), conversionService);
        final ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBeansWithAnnotation(Injectable.class)).thenReturn(Map.of());
        underTest.setApplicationContext(applicationContext);
        underTest.afterPropertiesSet();

        //when
        final Predicate<Object> actual = underTest.lookupPredicate(
                Map.of(NAME, ANY_STRING));

        //then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(AnyStringPredicate.class, actual.getClass());
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @MethodSource("invalidMapProvider")
    void testLookupPredicateShouldThrowExceptionWhenPredicateNotFound(final Map<String, RawConfigParam> map,
                                                                      final Class<? extends Exception> exception) throws Exception {
        //given
        final ConversionService conversionService = mock(ConversionService.class);
        when(conversionService.convert(any(), any(Class.class))).thenAnswer(a -> a.getArgument(0));
        final FunctionRegistry underTest = new FunctionRegistryImpl(Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), conversionService);
        final ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBeansWithAnnotation(Injectable.class)).thenReturn(Map.of());
        underTest.setApplicationContext(applicationContext);
        underTest.afterPropertiesSet();
        underTest.registerPredicateClass(FailingPredicate.class);

        //when + then exception
        Assertions.assertThrows(exception, () -> underTest.lookupPredicate(map));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(classes = {WrongSupplier.class, FailingSupplier.class})
    void testRegisterSupplierClassShouldThrowExceptionForInvalidInput(final Class<? extends Supplier<?>> clazz) throws Exception {
        //given
        final ConversionService conversionService = mock(ConversionService.class);
        final FunctionRegistry underTest = new FunctionRegistryImpl(Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), conversionService);
        final ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBeansWithAnnotation(Injectable.class)).thenReturn(Map.of());
        underTest.setApplicationContext(applicationContext);
        underTest.afterPropertiesSet();
        underTest.registerSupplierClass(FailingSupplier.class);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.registerSupplierClass(clazz));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(classes = {WrongFunction.class, FailingFunction.class})
    void testRegisterFunctionClassShouldThrowExceptionForInvalidInput(final Class<? extends Function<?, ?>> clazz) throws Exception {
        //given
        final ConversionService conversionService = mock(ConversionService.class);
        final FunctionRegistry underTest = new FunctionRegistryImpl(Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), conversionService);
        final ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBeansWithAnnotation(Injectable.class)).thenReturn(Map.of());
        underTest.setApplicationContext(applicationContext);
        underTest.afterPropertiesSet();
        underTest.registerFunctionClass(FailingFunction.class);

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> underTest.registerFunctionClass(clazz));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(classes = {WrongPredicate.class, FailingPredicate.class})
    void testPredicateFunctionClassShouldThrowExceptionForInvalidInput(final Class<? extends Predicate<?>> clazz) throws Exception {
        //given
        final ConversionService conversionService = mock(ConversionService.class);
        final FunctionRegistry underTest = new FunctionRegistryImpl(Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), conversionService);
        final ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBeansWithAnnotation(Injectable.class)).thenReturn(Map.of());
        underTest.setApplicationContext(applicationContext);
        underTest.afterPropertiesSet();
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

    private static final class FailingSupplier extends WrongSupplier {
        @NamedSupplier(FAILING)
        private FailingSupplier(@ValueParam(NONE) final String something,
                                @ValueParam(value = UNKNOWN, nullable = true) final String unknown) {
            if (something.isBlank()) {
                throw new RuntimeException(something);
            }
        }
    }

    private static class WrongFunction implements Function<String, String> {
        @Override
        public String apply(final String s) {
            return null;
        }
    }

    private static final class FailingFunction extends WrongFunction {
        @NamedFunction(FAILING)
        private FailingFunction(@ValueParam(NONE) final String something,
                                @ValueParam(value = UNKNOWN, nullable = true) final String unknown) {
            if (something.isBlank()) {
                throw new RuntimeException(something);
            }
        }
    }

    private static class WrongPredicate implements Predicate<String> {
        @Override
        public boolean test(final String s) {
            return false;
        }
    }

    private static final class FailingPredicate extends WrongPredicate {
        @NamedPredicate(FAILING)
        private FailingPredicate(@ValueParam(NONE) final String something,
                                 @ValueParam(value = UNKNOWN, nullable = true) final String unknown) {
            if (something.isBlank()) {
                throw new RuntimeException(something);
            }
        }
    }
}
