package com.github.nagyesta.yippeekijson.metadata.schema.markdown.impl;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import net.steppschuh.markdowngenerator.MarkdownBuilder;
import net.steppschuh.markdowngenerator.text.TextBuilder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.lang.reflect.Type;
import java.util.Optional;

import static net.steppschuh.markdowngenerator.Markdown.code;

@LaunchAbortArmed
class BaseMarkdownGeneratorTest {

    public static final String WILDCARD_MAP = "[`Map`]"
            + "(https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html) < `?` , `?` > ";

    @Test
    void testJavaTypeReturnsValidTypeNameForUnusualTypes() {
        //given
        BaseMarkdownGenerator underTest = new BaseMarkdownGenerator() {
            @Override
            public boolean supports(@NotNull final ComponentContext componentContext) {
                return false;
            }
        };
        Type test = new WeirdType();

        //when
        final String actual = underTest.javaType(test);

        //then
        Assertions.assertEquals(code(WeirdType.class.getName()).toString(), actual);
    }

    @Test
    void testAppendResourceThrowsExceptionIfResourceNotFound() {
        //given
        BaseMarkdownGenerator underTest = new BaseMarkdownGenerator() {
            @Override
            public boolean supports(@NotNull final ComponentContext componentContext) {
                return false;
            }
        };
        MarkdownBuilder<?, ?> markdownBuilder = new TextBuilder();
        ClassPathResource resource = new ClassPathResource("not-found-for-sure.java");

        //when + then exception
        Assertions.assertThrows(IllegalStateException.class, () ->
                underTest.appendResource(markdownBuilder, Optional.of(resource), Language.JSON, "header"));
    }

    private static final class WeirdType implements Type {
        @Override
        public String getTypeName() {
            return WeirdType.class.getName();
        }
    }
}
