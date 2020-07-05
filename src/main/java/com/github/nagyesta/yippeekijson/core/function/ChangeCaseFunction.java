package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
import com.github.nagyesta.yippeekijson.core.function.helper.CaseChange;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.Example;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.SchemaDefinition;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.WikiLink;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.StringJoiner;
import java.util.function.Function;

/**
 * {@link Function} for changing the case of some {@link String} values.
 */
@Slf4j
public final class ChangeCaseFunction implements Function<String, String> {

    static final String NAME = "changeCase";

    private final CaseChange to;

    @SchemaDefinition(
            inputType = String.class,
            outputType = String.class,
            sinceVersion = WikiConstants.VERSION_1_1_0,
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_FUNCTIONS, section = "Change case"),
            description = {
                    "This function takes the input value and changes the case as defined by the \"to\" parameter."
            },
            example = @Example(
                    in = "/examples/json/simple-accounts_in.json",
                    out = "/examples/json/add-string-capitalized.json",
                    yml = "/examples/yml/add-string-capitalized.yml",
                    note = "This example shows that the inserted \"Missing\" value is capitalized with this function."
            )
    )
    @NamedFunction(NAME)
    public ChangeCaseFunction(@ValueParam(docs = "Defines what is the desired case change operation we want to do.")
                              @NonNull final CaseChange to) {
        this.to = to;
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

}
