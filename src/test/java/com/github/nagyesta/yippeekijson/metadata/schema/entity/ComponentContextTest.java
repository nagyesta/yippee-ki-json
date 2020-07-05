package com.github.nagyesta.yippeekijson.metadata.schema.entity;

import com.github.nagyesta.yippeekijson.metadata.schema.entity.typehelper.StringObjectMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class ComponentContextTest {

    private static final String LEVEL_1 = "level1";
    private static final String LEVEL_2 = "level2";
    private static final String WRAPPER_OBJECT = "Wrapper object.";
    private static final String PARAM_42 = "Answer to the Ultimate Question of Life, the Universe, and Everything";
    private static final String LEVEL_1_LEVEL_2 = LEVEL_1 + "." + LEVEL_2;

    @Test
    void testBuilderMergePropertiesMergesChildStructuresProperlyWhenTopLevelPresent() {
        //given
        final ComponentContext.ComponentContextBuilder underTest = ComponentContext.builder()
                .properties(LEVEL_1, PropertyContext.builder()
                        .name(LEVEL_1)
                        .type(StringObjectMap.class)
                        .build())
                .propertiesMerge(new String[]{LEVEL_1, LEVEL_2}, PropertyContext.builder()
                        .name(LEVEL_1)
                        .docs(Optional.of(WRAPPER_OBJECT))
                        .child(Optional.of(PropertyContext.builder()
                                .name(LEVEL_2)
                                .type(Integer.class)
                                .docs(Optional.of(PARAM_42))
                                .build()))
                        .build());

        //when
        final ComponentContext actual = underTest.build();

        //then
        assertMergeSuccessful(actual);
    }

    @Test
    void testBuilderMergePropertiesMergesChildStructuresProperlyWhenExactMatchPresent() {
        //given
        final ComponentContext.ComponentContextBuilder underTest = ComponentContext.builder()
                .properties(LEVEL_1_LEVEL_2, PropertyContext.builder()
                        .name(LEVEL_1)
                        .type(StringObjectMap.class)
                        .child(Optional.of(PropertyContext.builder()
                                .name(LEVEL_2)
                                .type(Object.class)
                                .build()))
                        .build())
                .propertiesMerge(new String[]{LEVEL_1, LEVEL_2}, PropertyContext.builder()
                        .name(LEVEL_1)
                        .docs(Optional.of(WRAPPER_OBJECT))
                        .child(Optional.of(PropertyContext.builder()
                                .name(LEVEL_2)
                                .type(Integer.class)
                                .docs(Optional.of(PARAM_42))
                                .build()))
                        .build());

        //when
        final ComponentContext actual = underTest.build();

        //then
        assertMergeSuccessful(actual);
    }

    private void assertMergeSuccessful(final ComponentContext actual) {
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getProperties());
        Assertions.assertEquals(1, actual.getProperties().size());
        Assertions.assertNull(actual.getProperties().get(LEVEL_1));
        final PropertyContext level1 = actual.getProperties().get(LEVEL_1_LEVEL_2);
        Assertions.assertNotNull(level1);
        Assertions.assertTrue(level1.getDocs().isPresent());
        Assertions.assertEquals(WRAPPER_OBJECT, level1.getDocs().get());
        Assertions.assertEquals(StringObjectMap.class, level1.getType());
        Assertions.assertTrue(level1.getChild().isPresent());
        final PropertyContext level2 = level1.getChild().get();
        Assertions.assertNotNull(level2);
        Assertions.assertTrue(level2.getDocs().isPresent());
        Assertions.assertEquals(PARAM_42, level2.getDocs().get());
        Assertions.assertEquals(Integer.class, level2.getType());
    }
}
