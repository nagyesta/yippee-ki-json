package com.github.nagyesta.yippeekijson.core.config.entities;

import com.github.nagyesta.yippeekijson.core.rule.JsonRule;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the configuration of a single action.
 */
public class JsonAction {

    private final String name;
    private final List<JsonRule> rules;

    JsonAction(@NonNull final JsonActionBuilder builder) {
        this.name = builder.name;
        this.rules = List.copyOf(builder.rules);
    }

    public static JsonActionBuilder builder() {
        return new JsonActionBuilder();
    }

    /**
     * Provides the name of the action.
     *
     * @return the name of the action
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns an unmodifiable list of the rules defined under this action.
     *
     * @return the list of rules.
     */
    public List<JsonRule> getRules() {
        return Collections.unmodifiableList(this.rules);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JsonAction.class.getSimpleName() + "[", "]")
                .add("rules=\n\t" + rules.stream().map(Objects::toString).collect(Collectors.joining("\t")))
                .toString();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static class JsonActionBuilder {
        private String name;
        private List<JsonRule> rules;

        JsonActionBuilder() {
            reset();
        }

        private void reset() {
            rules = new ArrayList<>();
        }

        public JsonAction.JsonActionBuilder name(@NonNull final String name) {
            this.name = name;
            return this;
        }

        public JsonAction.JsonActionBuilder addRule(@NonNull final JsonRule rule) {
            this.rules.add(rule);
            Collections.sort(this.rules);
            return this;
        }

        public JsonAction build() {
            final JsonAction action = new JsonAction(this);
            reset();
            return action;
        }
    }
}
