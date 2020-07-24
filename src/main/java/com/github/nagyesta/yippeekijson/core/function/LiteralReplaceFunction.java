package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.StringJoiner;
import java.util.function.Function;

/**
 * {@link Function} for replacing {@link String} values using literals.
 */
@Slf4j
public final class LiteralReplaceFunction implements Function<String, String> {

    static final String NAME = "replace";

    private final String find;
    private final String replace;

    @SchemaDefinition(
            inputType = String.class,
            outputType = String.class,
            sinceVersion = WikiConstants.VERSION_1_1_0,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_FUNCTIONS, section = "Literal replace function"),
            description = {
                    "This function takes the input value, attempts to replace all occurrences of the \"find\"",
                    "value with the \"replace\" value."
            },
            example = @Example(
                    in = "/examples/json/account_replace_in.json",
                    out = "/examples/json/account_replace_out.json",
                    yml = "/examples/yml/replace.yml",
                    note = "In this example, our replace function removed the \" Doe\" part from both \"firstName\" fields."
            )
    )
    @NamedFunction(NAME)
    public LiteralReplaceFunction(@ValueParam(docs = "A literal we need to find in the input String.")
                                  @NonNull final String find,
                                  @ValueParam(docs = "The replacement we need to use.")
                                  @NonNull final String replace) {
        this.find = find;
        this.replace = replace;
    }

    @Override
    public String apply(final String s) {
        return StringUtils.replace(s, find, replace);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LiteralReplaceFunction.class.getSimpleName() + "[", "]")
                .add("find='" + find + "'")
                .add("replace='" + replace + "'")
                .toString();
    }
}
