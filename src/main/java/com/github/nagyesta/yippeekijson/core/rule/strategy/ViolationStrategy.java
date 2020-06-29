package com.github.nagyesta.yippeekijson.core.rule.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Defines the possible strategies to handle violations.
 */
@Slf4j
public enum ViolationStrategy {
    /**
     * Logs the findings to the default log.
     */
    LOG_ONLY() {
        @Override
        public void accept(@NotNull final DocumentContext documentContext,
                           @NotNull final Set<ValidationMessage> violations) {
            if (CollectionUtils.isEmpty(violations)) {
                return;
            }
            violations.stream()
                    .map(ValidationMessage::getMessage)
                    .forEach(log::error);
        }
    },
    /**
     * Comments the validation results into the JSON itself. Finds the closest possible object by stepping up on
     * the hierarchy of the node that failed to mitigate that some nodes cannot contain fields.
     */
    COMMENT_JSON() {
        @Override
        public void accept(@NotNull final DocumentContext documentContext,
                           @NotNull final Set<ValidationMessage> violations) {
            if (CollectionUtils.isEmpty(violations)) {
                return;
            }
            violations.forEach(validationMessage -> {
                final String path = validationMessage.getPath();
                Optional<JsonPath> target = findClosestObjectNode(documentContext, path);
                if (target.isEmpty()) {
                    log.error("Cannot append node to: " + validationMessage.getPath());
                    log.error(validationMessage.getMessage());
                } else {
                    final JsonPath jsonPath = target.get();
                    final Map<String, Object> map = documentContext.read(jsonPath, JsonMapper.MapTypeRef.INSTANCE);
                    if (!map.containsKey(VALIDATION_NODE)) {
                        documentContext.put(jsonPath, VALIDATION_NODE, new ArrayList<>());
                    }
                    documentContext.add(JsonPath.compile(jsonPath.getPath() + DOT + VALIDATION_NODE),
                            Map.of(PATH_FIELD_NAME, validationMessage.getPath(),
                                    MESSAGE_FIELD_NAME, validationMessage.getMessage()));
                }
            });
        }

        private Optional<JsonPath> findClosestObjectNode(@NotNull final DocumentContext documentContext,
                                                         @NotNull final String path) {
            Optional<JsonPath> jsonPath = Optional.ofNullable(safeCompile(path));
            if (jsonPath.isPresent() && isNotAnObject(documentContext, jsonPath.get())) {
                jsonPath = continueUntilRootFound(documentContext, path);
            }
            return jsonPath;
        }

        private Optional<JsonPath> continueUntilRootFound(@NotNull final DocumentContext documentContext,
                                                          @NotNull final String path) {
            Optional<JsonPath> jsonPath;
            if (ROOT_NODE.equalsIgnoreCase(path)) {
                jsonPath = Optional.empty();
            } else {
                jsonPath = findClosestObjectNode(documentContext, StringUtils.substringBeforeLast(path, DOT));
            }
            return jsonPath;
        }

        private boolean isNotAnObject(@NotNull final DocumentContext documentContext,
                                      @NotNull final JsonPath jsonPath) {
            try {
                final JsonNode read = documentContext.read(jsonPath, JsonNode.class);
                return !read.isObject();
            } catch (final PathNotFoundException e) {
                return true;
            }
        }

        private JsonPath safeCompile(@NotNull final String path) {
            try {
                return JsonPath.compile(path);
            } catch (final Exception e) {
                log.error(e.getMessage(), e);
                return null;
            }
        }
    },

    /**
     * Ignores the output.
     */
    IGNORE() {
        @Override
        public void accept(@NotNull final DocumentContext documentContext,
                           @NotNull final Set<ValidationMessage> violations) {
            if (CollectionUtils.isEmpty(violations)) {
                return;
            }
            log.info("Suppress: JSON document is invalid. " + violations.size() + " violations found.");
        }
    };

    static final String DOT = ".";
    static final String ROOT_NODE = "$";
    static final String VALIDATION_NODE = "$_yippee-schema-violation";
    static final String MESSAGE_FIELD_NAME = "message";
    static final String PATH_FIELD_NAME = "path";

    /**
     * Evaluates the validation result and triggers a response if needed.
     *
     * @param documentContext The document we are processing.
     * @param violations      The validation results.
     */
    public abstract void accept(@NotNull DocumentContext documentContext,
                                @NotNull Set<ValidationMessage> violations);
}
