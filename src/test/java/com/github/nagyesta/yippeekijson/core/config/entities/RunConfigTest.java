package com.github.nagyesta.yippeekijson.core.config.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RunConfigTest {

    @Test
    void testConstructorShouldThrowExceptionsWhenCalledWithNull() {
        //given
        final RunConfig.RunConfigBuilder builder = null;

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            //noinspection ConstantConditions
            new RunConfig(builder);
        });
    }
}
