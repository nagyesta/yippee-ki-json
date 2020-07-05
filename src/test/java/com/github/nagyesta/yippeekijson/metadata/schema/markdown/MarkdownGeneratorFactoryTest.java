package com.github.nagyesta.yippeekijson.metadata.schema.markdown;

import com.github.nagyesta.yippeekijson.core.NamedComponentUtil;
import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.*;
import com.github.nagyesta.yippeekijson.metadata.schema.config.MetadataParserConfig;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentType;
import com.github.nagyesta.yippeekijson.metadata.schema.markdown.impl.FunctionMarkdownGenerator;
import com.github.nagyesta.yippeekijson.metadata.schema.markdown.impl.JsonRuleMarkdownGenerator;
import com.github.nagyesta.yippeekijson.metadata.schema.markdown.impl.PredicateMarkdownGenerator;
import com.github.nagyesta.yippeekijson.metadata.schema.markdown.impl.SupplierMarkdownGenerator;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

class MarkdownGeneratorFactoryTest {

    private static final String EXPECTED_MINIMAL_DOCS = "### test\n\n**Name:** `test`\n\n**Class:** [`TestPredicate`]"
            + "(https://github.com/nagyesta/yippee-ki-json/blob/main/src/main/java/com/github/nagyesta/yippeekijson/"
            + "metadata/schema/markdown/MarkdownGeneratorFactoryTest$TestPredicate.java)\n\n**Since version:** 1.0.0\n"
            + "\n**Input type:** [`Object`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html)\n"
            + "\n**Parameters**\n\n| Name  | Type                                                                              "
            + "       | Description               |\n| ----- | ----------------------------------------------------------------"
            + "------------------------ | ------------------------- |\n| `map` | [`Map`](https://docs.oracle.com/en/java/javase/"
            + "11/docs/api/java.base/java/util/Map.html) | < Missing documentation > |\n\n**Description**\n\n\n";

    @Test
    void testRenderComponentThrowsExceptionIfSupportsCallFails() {
        //given
        final PredicateMarkdownGenerator predicateMarkdownGenerator = new PredicateMarkdownGenerator();
        MarkdownGeneratorFactory underTest = new MarkdownGeneratorFactory(predicateMarkdownGenerator);

        final ComponentContext componentContext = ComponentContext.builder()
                .componentType(ComponentType.RULE)
                .build();

        //when + then exception
        Assertions.assertThrows(IllegalStateException.class, () -> underTest.renderComponent(componentContext));
    }

    @Test
    void testSupportsReturnsFalseIfNoneOfTheGeneratorsSupportTheType() {
        //given
        final PredicateMarkdownGenerator predicateMarkdownGenerator = new PredicateMarkdownGenerator();
        final SupplierMarkdownGenerator supplierMarkdownGenerator = new SupplierMarkdownGenerator();
        final FunctionMarkdownGenerator functionMarkdownGenerator = new FunctionMarkdownGenerator();
        final JsonRuleMarkdownGenerator jsonRuleMarkdownGenerator = new JsonRuleMarkdownGenerator();
        MarkdownGeneratorFactory underTest = new MarkdownGeneratorFactory(
                predicateMarkdownGenerator, supplierMarkdownGenerator,
                functionMarkdownGenerator, jsonRuleMarkdownGenerator);

        @SuppressWarnings("ConstantConditions") final ComponentContext componentContext = ComponentContext.builder()
                .componentType(null)
                .build();

        //when
        boolean actual = underTest.supports(componentContext);

        //then
        Assertions.assertFalse(actual);
    }

    @Test
    void testRenderComponentsRendersMinimalMarkdownForTestClass() {
        //given
        final Optional<Constructor<?>> constructor = NamedComponentUtil
                .findAnnotatedConstructorOfNamedComponent(TestPredicate.class, NamedPredicate.class);
        //noinspection OptionalGetWithoutIsPresent
        final ComponentContext context = new MetadataParserConfig().componentContextMetadataParser().parse(constructor.get());

        final PredicateMarkdownGenerator predicateMarkdownGenerator = new PredicateMarkdownGenerator();
        MarkdownGeneratorFactory underTest = new MarkdownGeneratorFactory(predicateMarkdownGenerator);

        //when
        final String actual = underTest.renderComponent(context);

        //then
        Assertions.assertEquals(StringUtils.trimToEmpty(EXPECTED_MINIMAL_DOCS), StringUtils.trimToEmpty(actual));

    }

    private static final class TestPredicate implements Predicate<Object> {

        @SchemaDefinition(
                inputType = Object.class,
                properties = @PropertyDefinitions({
                        @PropertyDefinition(
                                name = "map",
                                type = @TypeDefinition(itemType = Map.class)
                        )
                }),
                wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_PREDICATES)
        )
        @NamedPredicate("test")
        private TestPredicate() {

        }

        @Override
        public boolean test(final Object o) {
            return false;
        }
    }
}
