package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.MethodParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link java.util.function.Function} for replacing {@link String} values using a RegExp.
 */
@Slf4j
public final class RegexReplaceFunction implements Function<String, String> {

    private static final Pattern NAMED_GROUP_PATTERN = Pattern.compile("(?:\\$\\{(?<groupName>[a-zA-Z0-9]+)})");
    private static final String GROUP_NAME_KEY = "groupName";
    static final String NAME = "regex";
    static final String PARAM_PATTERN = "pattern";
    static final String PARAM_REPLACEMENT = "replacement";

    private final Pattern pattern;
    private final String replacement;

    @NamedFunction(NAME)
    public RegexReplaceFunction(@MethodParam(PARAM_PATTERN) @NonNull final String pattern,
                                @MethodParam(PARAM_REPLACEMENT) @NonNull final String replacement) {
        this.pattern = Pattern.compile(pattern);
        this.replacement = replacement;
    }

    @Override
    public String apply(final String s) {
        if (s == null) {
            return null;
        }
        final Matcher matcher = pattern.matcher(s);
        if (!matcher.matches()) {
            log.warn(String.format("Pattern: \"%s\" does not match input: \"%s\". Ignoring.", pattern, s));
            return s;
        }

        String result = replacement;
        for (Matcher r = NAMED_GROUP_PATTERN.matcher(replacement); r.find(); r = NAMED_GROUP_PATTERN.matcher(result)) {
            try {
                final String groupName = r.group(GROUP_NAME_KEY);
                result = r.replaceFirst(matcher.group(groupName));
            } catch (final IllegalArgumentException e) {
                log.error(String.format("Pattern: \"%s\" replacement of input: \"%s\" failed due to: %s", pattern, s, e.getMessage()));
                return s;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RegexReplaceFunction.class.getSimpleName() + "[", "]")
                .add("pattern=" + pattern)
                .add("replacement='" + replacement + "'")
                .toString();
    }
}
