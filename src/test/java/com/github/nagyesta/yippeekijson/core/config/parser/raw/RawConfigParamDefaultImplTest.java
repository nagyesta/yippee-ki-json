package com.github.nagyesta.yippeekijson.core.config.parser.raw;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RawConfigParamDefaultImplTest {

    private final RawConfigParam underTest = new RawConfigParam() {
        @Override
        public @NotNull String getConfigPath() {
            return "null";
        }

        @Override
        public boolean isRepeated() {
            return false;
        }

        @Override
        public boolean isMapType() {
            return false;
        }

        @Override
        public @NotNull Object suitableFor(final boolean stringMap, final boolean paramMap, final boolean repeat) {
            return "null";
        }
    };

    @Test
    void testAsStringShouldThrowException() {
        //given

        //when + then exception
        Assertions.assertThrows(UnsupportedOperationException.class, underTest::asString);
    }

    @Test
    void testAsStringsShouldThrowException() {
        //given

        //when + then exception
        Assertions.assertThrows(UnsupportedOperationException.class, underTest::asStrings);
    }

    @Test
    void testAsMapShouldThrowException() {
        //given

        //when + then exception
        Assertions.assertThrows(UnsupportedOperationException.class, underTest::asMap);
    }

    @Test
    void testAsMapsShouldThrowException() {
        //given

        //when + then exception
        Assertions.assertThrows(UnsupportedOperationException.class, underTest::asMaps);
    }
}
