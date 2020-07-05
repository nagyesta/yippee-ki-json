package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.StringJoiner;
import java.util.function.Predicate;

/**
 * {@link Predicate} using SpEL to evaluate expressions on {@link Object} values.
 */
public final class SpringExpressionLanguagePredicate implements Predicate<Object> {

    static final String NAME = "SpEL";

    private final SpelExpression expression;

    @SchemaDefinition(
            inputType = Object.class,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_PREDICATES, section = "SpEL predicate"),
            sinceVersion = WikiConstants.VERSION_1_1_0,
            description = {
                    "This predicate returns true if input value matches the SpEL expression defined in the \"expression\" parameter."
            },
            example = @Example(
                    in = "/examples/json/blog-entries_in.json",
                    out = "/examples/json/blog-entries_subtract-likes_out.json",
                    yml = "/examples/yml/calculate-subtract-likes.yml",
                    note = {
                            "The example shows how this predicate help us to exclude the node where the reduction of 3 would",
                            "have resulted in an invalid value considering that negative number of likes is not possible."
                    })
    )
    @NamedPredicate(NAME)
    public SpringExpressionLanguagePredicate(@ValueParam(docs = "The SpEL expression we want to match.")
                                             @NonNull final String expression) {
        try {
            this.expression = new SpelExpressionParser().parseRaw(expression);
        } catch (final Exception e) {
            throw new IllegalArgumentException("Invalid SpEL expression found: " + expression, e);
        }
    }

    @Override
    public boolean test(@Nullable final Object o) {
        try {
            final Boolean expressionValue = expression.getValue(new StandardEvaluationContext(o), Boolean.class);
            return o != null && expressionValue != null && expressionValue;
        } catch (final Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SpringExpressionLanguagePredicate.class.getSimpleName() + "[", "]")
                .add("expression=" + expression.getExpressionString())
                .toString();
    }
}
