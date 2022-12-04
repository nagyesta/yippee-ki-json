package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.nagyesta.yippeekijson.core.config.entities.JsonAction;
import com.github.nagyesta.yippeekijson.core.config.entities.JsonActions;
import com.github.nagyesta.yippeekijson.core.config.parser.ActionConfigParser;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonRuleRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonAction;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonActions;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Component
@Slf4j
public class YamlActionConfigParser implements ActionConfigParser {

    private static final String BUNDLED_SCHEMA = "/yippee-ki-json_config_schema.json";
    private final JsonRuleRegistry ruleRegistry;

    private final Validator validator;

    @Autowired
    public YamlActionConfigParser(@NonNull final JsonRuleRegistry ruleRegistry,
                                  @NonNull final Validator validator) {
        this.ruleRegistry = ruleRegistry;
        this.validator = validator;
    }

    @Override
    public JsonActions parse(@NonNull final InputStream stream, final boolean relaxed) throws ConfigParseException {
        try {
            final RawJsonActions rawJsonActions = parseAsRawJsonActions(stream, relaxed);

            final int parsedActions = Optional.ofNullable(rawJsonActions).map(RawJsonActions::getActions).map(List::size).orElse(0);
            log.info("Parsed " + parsedActions + " actions.");

            return convertActions(rawJsonActions);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new ConfigParseException("failed to parse configuration.", e);
        }
    }

    @Override
    public JsonActions parse(@NonNull final File config, final boolean relaxed) throws ConfigParseException {
        log.info("Parsing configuration: " + config.getAbsolutePath());
        try (FileInputStream inputStream = new FileInputStream(config)) {
            return parse(inputStream, relaxed);
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
            throw new ConfigParseException("IOException happened while parsing configuration file.", e);
        }
    }

    /**
     * Parses a stream into {@link RawJsonActions} for further processing.
     *
     * @param stream  The input
     * @param relaxed Turns off the strict schema validation
     * @return The parsed object
     * @throws ConfigParseException when the raw data is invalid
     */
    protected RawJsonActions parseAsRawJsonActions(@NotNull final InputStream stream, final boolean relaxed) throws ConfigParseException {
        final RawJsonActions rawJsonActions = parseYaml(stream, relaxed);
        final Set<ConstraintViolation<RawJsonActions>> violations = validator.validate(rawJsonActions);
        if (!violations.isEmpty()) {
            violations.forEach(violation -> log.error("Yml validation error at: " + violation.getPropertyPath()
                    + " message: " + violation.getMessage()));
            throw new ConfigParseException("Yaml configuration is invalid.");
        }
        return rawJsonActions;
    }

    private RawJsonActions parseYaml(@NotNull final InputStream stream, final boolean relaxed) throws ConfigParseException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        JsonSchemaFactory factory = JsonSchemaFactory
                .builder(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7))
                .objectMapper(mapper)
                .build();

        try (InputStream schemaStream = YamlActionConfigParser.class.getResourceAsStream(BUNDLED_SCHEMA)) {
            JsonSchema schema = factory.getSchema(schemaStream);
            JsonNode jsonNode = mapper.readTree(stream);
            Set<ValidationMessage> violations = schema.validate(jsonNode);
            reportViolations(violations, relaxed);
            return mapper.treeToValue(jsonNode, RawJsonActions.class);
        } catch (final IOException e) {
            throw new ConfigParseException(e.getMessage(), e);
        }
    }

    private void reportViolations(final Set<ValidationMessage> violations, final boolean relaxed) throws ConfigParseException {
        if (!CollectionUtils.isEmpty(violations)) {
            if (relaxed) {
                logViolations(violations, log::warn);
            } else {
                logViolations(violations, log::error);
                throw new ConfigParseException(MessageFormat
                        .format("Configuration YML is invalid. {0} violation(s) found. {1}",
                                violations.size(), "Run with --yippee.relaxed-yml-schema=true to suppress this."));
            }
        }
    }

    private void logViolations(final Set<ValidationMessage> violations, final Consumer<? super String> consumer) {
        violations.stream()
                .map(ValidationMessage::getMessage)
                .forEach(consumer);
    }

    /**
     * Converts an instance of {@link RawJsonActions} into a parsed object.
     *
     * @param rawJsonActions The RAW format
     * @return the converted actions.
     */
    protected JsonActions convertActions(@Nullable final RawJsonActions rawJsonActions) {
        final JsonActions.JsonActionsBuilder builder = JsonActions.builder();
        if (rawJsonActions != null) {
            rawJsonActions.getActions().stream()
                    .map(this::convertSingleAction)
                    .filter(Objects::nonNull)
                    .forEach(a -> builder.addAction(a.getName(), a));
        }
        final JsonActions actions = builder.build();
        if (log.isDebugEnabled()) {
            log.debug("Converted actions: " + actions);
        } else {
            log.info("Converted actions: " + actions.getActions().keySet());
        }
        return actions;
    }

    /**
     * Converts a single {@link RawJsonAction} into an action.
     *
     * @param rawJsonAction The RAW format
     * @return the converted action.
     */
    protected JsonAction convertSingleAction(final RawJsonAction rawJsonAction) {
        if (rawJsonAction == null) {
            return null;
        }
        reindexRules(rawJsonAction);

        log.info("Converting " + rawJsonAction.getRules().size() + " rules for action named: " + rawJsonAction.getName());
        final JsonAction.JsonActionBuilder actionBuilder = JsonAction.builder()
                .name(rawJsonAction.getName());

        rawJsonAction.getRules().stream()
                .map(ruleRegistry::newInstanceFrom)
                .forEach(actionBuilder::addRule);

        return actionBuilder.build();
    }

    /**
     * Reindexes rules if the given action.
     *
     * @param action the action holding the rules
     */
    protected void reindexRules(final RawJsonAction action) {
        final AtomicInteger index = new AtomicInteger(0);
        action.getRules().forEach(rule -> rule.setOrder(index.getAndIncrement()));
    }
}
