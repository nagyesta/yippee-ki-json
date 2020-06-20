package com.github.nagyesta.yippeekijson.core.config.parser.raw;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

class RawJsonRuleTest {

    private static final String NAME = "name";
    private static final String PATH = "path";

    private static Object[][] nullProvider() {
        return new Object[][]{
                {null, null, null, null},
                {1, null, null, null},
                {null, NAME, null, null},
                {null, null, PATH, null},
                {null, null, null, Collections.emptyMap()},
                {1, NAME, null, null},
                {null, null, PATH, Collections.emptyMap()},
                {1, NAME, PATH, null},
                {1, NAME, null, Collections.emptyMap()},
                {1, null, PATH, Collections.emptyMap()},
                {null, NAME, PATH, Collections.emptyMap()}
        };
    }

    private static Stream<Arguments> negativeConfigParamMapSource() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(null, Collections.emptyMap()))
                .add(Arguments.of(null, Map.of(NAME, Map.of(NAME, NAME))))
                .add(Arguments.of(StringUtils.EMPTY, Collections.emptyMap()))
                .add(Arguments.of(StringUtils.EMPTY, Map.of(NAME, Map.of(NAME, NAME))))
                .add(Arguments.of(StringUtils.SPACE, Collections.emptyMap()))
                .add(Arguments.of(StringUtils.SPACE, Map.of(NAME, Map.of(NAME, NAME))))
                .build();
    }

    @ParameterizedTest
    @MethodSource("nullProvider")
    void testSettersShouldThrowExceptionsWhenCalledWithNulls(
            final Integer order, final String name, final String path, final Map<String, Map<String, Object>> params) {
        //given
        final RawJsonRule underTest = new RawJsonRule();

        //when + then exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            underTest.setOrder(order);
            underTest.setName(name);
            underTest.setPath(path);
            underTest.setParams(params);
        });
    }

    @ParameterizedTest
    @MethodSource("negativeConfigParamMapSource")
    void testConfigParamMapShouldReturnEmptyMapIfKeyMissing(final String key, final Map<String, Map<String, Object>> map) {
        //given
        final RawJsonRule underTest = RawJsonRule.builder()
                .putParams(map)
                .build();

        //when
        final Map<String, RawConfigParam> actual = underTest.configParamMap(key);

        //then
        Assertions.assertEquals(Collections.<String, RawConfigParam>emptyMap(), actual);
    }
}
