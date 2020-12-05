package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.exception.AbortTransformationException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.github.nagyesta.yippeekijson.test.helper.TestResourceProvider.*;

@LaunchAbortArmed
class FileContentSupplierTest {

    @ParameterizedTest
    @ValueSource(strings = {JSON_EXAMPLE, JSON_VALIDATION_INPUT})
    void testGetShouldReturnTheStaticString(final String input) throws IOException {
        //given
        final File file = resource().asFile(input);
        final FileContentSupplier underTest = new FileContentSupplier(file.getAbsolutePath(), null);

        //when
        final String actual = underTest.get();

        //then
        String expected = resource().asString(input);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetShouldThrowAbortExceptionWhenSourceSupplierFails() {
        //given
        final FileContentSupplier underTest = new FileContentSupplier(StringUtils.EMPTY, StandardCharsets.UTF_8);

        //when + then exception
        Assertions.assertThrows(AbortTransformationException.class, underTest::get);
    }

    @Test
    void testConstructorShouldNotAllowNulls() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new FileContentSupplier(null, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {JSON_EXAMPLE, JSON_VALIDATION_INPUT})
    void testToStringShouldContainClassNameAndKey(final String path) {
        //given
        final FileContentSupplier underTest = new FileContentSupplier(path, StandardCharsets.UTF_8);

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(FileContentSupplier.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(path));
        Assertions.assertTrue(actual.contains(StandardCharsets.UTF_8.name()));
    }
}
