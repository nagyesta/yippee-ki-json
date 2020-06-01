package com.github.nagyesta.yippeekijson.core.config.parser;

import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonRule;
import com.github.nagyesta.yippeekijson.core.rule.JsonRule;
import lombok.NonNull;

/**
 * Registry for the known {@link JsonRule} implementations we will be able to parse.
 */
public interface JsonRuleRegistry {

    /**
     * Attempts to find a previously registered rule class by name and instantiates it.
     *
     * @param jsonRule The source object with all of the parsed rule metadata.
     * @return The {@link JsonRule} implementation matching the provided name.
     */
    JsonRule newInstanceFrom(@NonNull RawJsonRule jsonRule);

    /**
     * Registers a {@link JsonRule} implementation to allow future use when parsing.
     *
     * @param rule The class we want to register.
     */
    void registerRuleClass(@NonNull Class<? extends JsonRule> rule);

}
