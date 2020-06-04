package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.MethodParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * {@link Function} for changing the case of some {@link String} values.
 */
@Slf4j
public final class ChangeCaseFunction implements Function<String, String> {

    static final String NAME = "changeCase";
    static final String PARAM_TO = "to";

    private final Case to;

    @NamedFunction(NAME)
    public ChangeCaseFunction(@MethodParam(PARAM_TO) @NonNull final String to) {
        this.to = Case.parse(to);
    }

    @Override
    public String apply(final String s) {
        return to.apply(s);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ChangeCaseFunction.class.getSimpleName() + "[", "]")
                .add("to='" + to.name() + "'")
                .toString();
    }

    protected enum Case {
        /**
         * Capitalizes the input.
         */
        CAPITALIZED(StringUtils::capitalize),
        /**
         * Un-capitalizes the input.
         */
        UNCAPITALIZED(StringUtils::uncapitalize),
        /**
         * Switches the input to all lower case.
         */
        LOWER_CASE(String::toLowerCase),
        /**
         * Switches the input to all upper case.
         */
        UPPER_CASE(String::toUpperCase);

        private final Function<String, String> function;

        Case(final Function<String, String> function) {
            this.function = function;
        }

        static Case parse(final String s) {
            return Arrays.stream(Case.values())
                    .filter(c -> c.name().equalsIgnoreCase(s))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown case option found: " + s));
        }

        String apply(final String string) {
            return Optional.ofNullable(string)
                    .map(function)
                    .orElse(null);
        }
    }
}
