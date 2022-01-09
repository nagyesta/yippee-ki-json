package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.function.DecimalAddFunction;
import com.github.nagyesta.yippeekijson.core.predicate.NoneMatchPredicate;
import com.github.nagyesta.yippeekijson.core.supplier.ConvertingSupplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FunctionRegistryWrapperTest {

    @Test
    void testRegisterSupplierClassShouldThrowExceptionWhenCalled() {
        //given
        final FunctionRegistryWrapper underTest = new FunctionRegistryWrapper();

        //when
        Assertions.assertThrows(UnsupportedOperationException.class, () -> underTest.registerSupplierClass(ConvertingSupplier.class));

        //then + exception
    }

    @Test
    void testRegisterFunctionClassShouldThrowExceptionWhenCalled() {
        //given
        final FunctionRegistryWrapper underTest = new FunctionRegistryWrapper();

        //when
        Assertions.assertThrows(UnsupportedOperationException.class, () -> underTest.registerFunctionClass(DecimalAddFunction.class));

        //then + exception
    }

    @Test
    void testRegisterPredicateClassShouldThrowExceptionWhenCalled() {
        //given
        final FunctionRegistryWrapper underTest = new FunctionRegistryWrapper();

        //when
        Assertions.assertThrows(UnsupportedOperationException.class, () -> underTest.registerPredicateClass(NoneMatchPredicate.class));

        //then + exception
    }
}
