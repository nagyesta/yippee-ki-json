package com.github.nagyesta.yippeekijson.core.config.entities;

import lombok.NonNull;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Represents the root of the configuration.
 */
public final class JsonActions {

    private final Map<String, JsonAction> actions;

    JsonActions(@NonNull final JsonActionsBuilder builder) {
        this.actions = Map.copyOf(builder.actions);
    }

    public static JsonActionsBuilder builder() {
        return new JsonActionsBuilder();
    }

    public Map<String, JsonAction> getActions() {
        return Collections.unmodifiableMap(this.actions);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JsonActions.class.getSimpleName() + "[", "]")
                .add("actions={\n" + actions.entrySet().stream()
                        .map(e -> e.getKey() + ": " + e.getValue()).collect(Collectors.joining(",\n")) + "\n}")
                .toString();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static class JsonActionsBuilder {
        private Map<String, JsonAction> actions;

        JsonActionsBuilder() {
            reset();
        }

        public JsonActionsBuilder addAction(@NonNull final String name, @NonNull final JsonAction action) {
            Assert.isTrue(!this.actions.containsKey(name), "Duplicate action found: " + name);
            this.actions.put(name, action);
            return this;
        }

        public JsonActions build() {
            final JsonActions newInstance = new JsonActions(this);
            reset();
            return newInstance;
        }

        private void reset() {
            this.actions = new TreeMap<>();
        }
    }
}
