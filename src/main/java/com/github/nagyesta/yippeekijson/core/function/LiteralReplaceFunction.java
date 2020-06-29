package com.github.nagyesta.yippeekijson.core.function;

import com.github.nagyesta.yippeekijson.core.annotation.NamedFunction;
import com.github.nagyesta.yippeekijson.core.annotation.ValueParam;
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
    static final String PARAM_FIND = "find";
    static final String PARAM_REPLACE = "replace";

    private final String find;
    private final String replace;

    @NamedFunction(NAME)
    public LiteralReplaceFunction(@ValueParam(PARAM_FIND) @NonNull final String find,
                                  @ValueParam(PARAM_REPLACE) @NonNull final String replace) {
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
