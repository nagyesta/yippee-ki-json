package com.github.nagyesta.yippeekijson.core.supplier;

import com.github.nagyesta.yippeekijson.core.exception.AbortTransformationException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

class FileContentSupplierTest {

    private static final String VALIDATION_INPUT_JSON = "/validation/validation-input.json";
    private static final String EXAMPLE_JSON = "/json/example.json";

    @ParameterizedTest
    @ValueSource(strings = {EXAMPLE_JSON, VALIDATION_INPUT_JSON})
    void testGetShouldReturnTheStaticString(final String input) throws IOException {
        //given
        final File file = new File(this.getClass().getResource(input).getFile());
        String expected = IOUtils.resourceToString(input, StandardCharsets.UTF_8);
        final FileContentSupplier underTest = new FileContentSupplier(file.getAbsolutePath(), null);

        //when
        final String actual = underTest.get();

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetShouldThrowAbortExceptionWhenSourceSupplierFails() {
        //given
        final FileContentSupplier underTest = new FileContentSupplier(StringUtils.EMPTY, StandardCharsets.UTF_8.name());

        //when + then exception
        Assertions.assertThrows(AbortTransformationException.class, underTest::get);
    }

    @Test
    void testConstructorShouldNotAllowNulls() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new FileContentSupplier(null, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {VALIDATION_INPUT_JSON, EXAMPLE_JSON})
    void testToStringShouldContainClassNameAndKey(final String path) {
        //given
        final FileContentSupplier underTest = new FileContentSupplier(path, StandardCharsets.UTF_8.name());

        //when
        final String actual = underTest.toString();

        //then
        Assertions.assertTrue(actual.contains(FileContentSupplier.class.getSimpleName()));
        Assertions.assertTrue(actual.contains(path));
        Assertions.assertTrue(actual.contains(StandardCharsets.UTF_8.name()));
    }
}
