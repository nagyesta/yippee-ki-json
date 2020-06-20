package com.github.nagyesta.yippeekijson.core.rule.impl;

import com.github.nagyesta.yippeekijson.core.annotation.NamedRule;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.predicate.NotNullPredicate;
import com.github.nagyesta.yippeekijson.core.rule.AbstractJsonRule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A simple rule do calculations on numeric valued ({@link BigDecimal}, {@link BigInteger} or a
 * {@link String} representing a number) fields at a {@link JsonPath}, matching a {@link Predicate}.
 */
@Slf4j
public final class JsonCalculateRule extends AbstractJsonRule {

    static final String RULE_NAME = "calculate";
    static final String PARAM_PREDICATE = "predicate";
    static final String PARAM_NUMBER_FUNCTION = "numberFunction";

    private final Predicate<Object> predicate;
    private final Function<BigDecimal, BigDecimal> numberFunction;

    @NamedRule(RULE_NAME)
    public JsonCalculateRule(@NotNull final FunctionRegistry functionRegistry,
                             @NotNull final RawJsonRule jsonRule) {
        super(jsonRule.getOrder(), JsonPath.compile(jsonRule.getPath()));
        this.predicate = functionRegistry.lookupPredicate(jsonRule.configParamMap(PARAM_PREDICATE), new NotNullPredicate());
        this.numberFunction = functionRegistry.lookupFunction(jsonRule.configParamMap(PARAM_NUMBER_FUNCTION));
    }

    @Override
    public void accept(@NotNull final DocumentContext documentContext) {
        documentContext.map(getJsonPath(), (currentValue, configuration) -> {
            Optional<BigDecimal> number = convertToNumber(currentValue);
            if (number.isPresent()) {
                final BigDecimal bigDecimal = number.get();
                if (predicate.test(bigDecimal)) {
                    return numberFunction.apply(bigDecimal);
                } else {
                    log.info(String.format("Object at jsonPath: \"%s\", did not match predicate. Ignoring.",
                            getJsonPath().getPath()));
                }
            }
            return currentValue;
        });
    }

    private Optional<BigDecimal> convertToNumber(final Object currentValue) {
        Optional<BigDecimal> result = Optional.empty();
        try {
            if (currentValue instanceof BigDecimal) {
                result = Optional.of((BigDecimal) currentValue);
            } else if (currentValue instanceof BigInteger) {
                result = Optional.of(new BigDecimal(currentValue.toString()));
            } else {
                result = Optional.of(new BigDecimal(String.valueOf(currentValue)));
            }
        } catch (final NumberFormatException e) {
            log.error(String.format("Attempted Number replace on jsonPath: \"%s\", found value: \"%s\". Ignoring.",
                    getJsonPath().getPath(), currentValue));
        }
        return result;
    }

    @Override
    public String toString() {
        return "JsonRule['" + RULE_NAME + "']"
                + "\n\t\torder: " + getOrder()
                + "\n\t\tpath: '" + getJsonPath().getPath() + "'"
                + "\n\t\tpredicate: " + predicate
                + "\n\t\tfunction: " + numberFunction
                + "\n";
    }

}
