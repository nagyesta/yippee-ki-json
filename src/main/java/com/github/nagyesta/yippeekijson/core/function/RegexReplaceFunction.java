package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
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

    static final String NAME = "regex";
    static final String PARAM_PATTERN = "pattern";
    static final String PARAM_REPLACEMENT = "replacement";
    private static final Pattern NAMED_GROUP_PATTERN = Pattern.compile("(?:\\$\\{(?<groupName>[a-zA-Z0-9]+)})");
    private static final String GROUP_NAME_KEY = "groupName";
    private final Pattern pattern;
    private final String replacement;

    @SuppressWarnings("DefaultAnnotationParam")
    @SchemaDefinition(
            inputType = String.class,
            outputType = String.class,
            sinceVersion = WikiConstants.VERSION_1_0_0,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_FUNCTIONS, section = "RegEx replace function"),
            description = {
                    "This function takes the input value, attempts to match the pre-defined pattern and capture the values of",
                    "the named capturing groups. Then using the replacement format it tries to resolve all EL-like placeholders",
                    "using the captured values form the previous step and returns the result."
            },
            example = @Example(
                    in = "/examples/json/account_replace_in.json",
                    out = "/examples/json/account_replace_out.json",
                    yml = "/examples/yml/replace.yml",
                    note = {"In this example, our regex replace function matches against two \"words\" using only letters and",
                            "dashes, separated by a single space. The replacement format tells the function to keep only the",
                            "second word and throw away the first. We applied this to both \"lastName\" fields."
                    }
            )
    )
    @NamedFunction(NAME)
    public RegexReplaceFunction(
            @ValueParam(docs = "A regex pattern that will be matched against the input, capturing certain named groups for later use.")
            @NonNull final String pattern,
            @ValueParam(docs = "Defines how the captured groups should be used to piece together the output value using EL-like syntax.")
            @NonNull final String replacement) {
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
