package com.github.nagyesta.yippeekijson.core.config.parser.raw;

import com.github.nagyesta.yippeekijson.core.config.parser.raw.params.RawConfigMap;
import com.github.nagyesta.yippeekijson.core.config.validation.JsonPath;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Raw class for parsing JsonRule configuration.
 */
@Getter
@Setter
public class RawJsonRule {
    @NonNull
    private Integer order;
    @NonNull
    @NotBlank
    private String name;
    @NonNull
    @JsonPath
    private String path;
    @NonNull
    @Valid
    private Map<String, Map<String, Object>> params = Collections.emptyMap();

    public RawJsonRule() {
    }

    private RawJsonRule(@NotNull final RawJsonRuleBuilder builder) {
        this.order = builder.order;
        this.name = builder.name;
        this.path = builder.path;
        this.params = Map.copyOf(builder.params);
    }

    public static RawJsonRuleBuilder builder() {
        return new RawJsonRuleBuilder();
    }

    /**
     * Converts the raw data read for a specific parameter into a preprocessed {@link Map}.
     *
     * @param param The name of the parameter.
     * @return The preprocessed map.
     */
    @NotNull
    public Map<String, RawConfigParam> configParamMap(@NonNull final String param) {
        if (CollectionUtils.isEmpty(params) || !params.containsKey(param)) {
            return Collections.emptyMap();
        }
        return new RawConfigMap(getConfigPath(param), params.get(param)).asMap();
    }

    @NotNull
    private String getConfigPath(@NotNull final String param) {
        return "rule[" + order + "](name='" + name + "')." + param;
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static class RawJsonRuleBuilder {
        private @NonNull Integer order;
        private @NonNull @NotBlank String name;
        private @NonNull String path;
        private @NonNull @Valid Map<String, Map<String, Object>> params;

        RawJsonRuleBuilder() {
            reset();
        }

        private void reset() {
            this.order = null;
            this.name = null;
            this.path = null;
            this.params = new HashMap<>();
        }

        public RawJsonRuleBuilder order(@NotNull final Integer order) {
            this.order = order;
            return this;
        }

        public RawJsonRuleBuilder name(@NotNull final String name) {
            this.name = name;
            return this;
        }

        public RawJsonRuleBuilder path(@NotNull final String path) {
            this.path = path;
            return this;
        }

        public RawJsonRuleBuilder putParams(@NotNull final Map<String, Map<String, Object>> params) {
            this.params.putAll(params);
            return this;
        }

        public RawJsonRule build() {
            final RawJsonRule rawJsonRule = new RawJsonRule(this);
            this.reset();
            return rawJsonRule;
        }

        public String toString() {
            return "RawJsonRule.RawJsonRuleBuilder(order=" + this.order
                    + ", name=" + this.name
                    + ", path=" + this.path
                    + ", params=" + this.params + ")";
        }
    }
}
