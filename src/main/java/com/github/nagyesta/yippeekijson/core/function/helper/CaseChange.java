package com.github.nagyesta.yippeekijson.core.function.helper;

import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.function.Function;

/**
 * Case change functions.
 */
public enum CaseChange {
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

    CaseChange(final Function<String, String> function) {
        this.function = function;
    }

    /**
     * Performs the case change operation.
     *
     * @param string input
     * @return the input with changed case
     */
    public String apply(final String string) {
        return Optional.ofNullable(string)
                .map(function)
                .orElse(null);
    }
}
