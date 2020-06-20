package com.github.nagyesta.yippeekijson.core.predicate;

import com.github.nagyesta.yippeekijson.core.annotation.NamedPredicate;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
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

    @NamedPredicate(NAME)
    public SpringExpressionLanguagePredicate(@ValueParam @NonNull final String expression) {
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
