package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.config.entities.JsonAction;
import com.github.nagyesta.yippeekijson.core.config.entities.JsonActions;
import com.github.nagyesta.yippeekijson.core.config.parser.ActionConfigParser;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonRuleRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonAction;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonActions;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class YamlActionConfigParser implements ActionConfigParser {

    private final JsonRuleRegistry ruleRegistry;

    @Autowired
    public YamlActionConfigParser(final JsonRuleRegistry ruleRegistry) {
        Assert.notNull(ruleRegistry, "ruleRegistry cannot be null.");
        this.ruleRegistry = ruleRegistry;
    }

    @Override
    public JsonActions parse(final InputStream stream) throws ConfigParseException {
        Assert.notNull(stream, "stream cannot be null.");
        try {
            final RawJsonActions rawJsonActions = parseAsRawJsonActions(stream);

            final int parsedActions = Optional.ofNullable(rawJsonActions).map(RawJsonActions::getActions).map(List::size).orElse(0);
            log.info("Parsed " + parsedActions + " actions.");

            return convertActions(rawJsonActions);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new ConfigParseException("failed to parse configuration.", e);
        }
    }

    @Override
    public JsonActions parse(final File config) throws ConfigParseException {
        Assert.notNull(config, "config file cannot be null.");

        log.info("Parsing configuration: " + config.getAbsolutePath());
        try (FileInputStream inputStream = new FileInputStream(config)) {
            return parse(inputStream);
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
            throw new ConfigParseException("IOException happened while parsing configuration file.", e);
        }
    }

    /**
     * Parses a stream into {@link RawJsonActions} for further processing.
     *
     * @param stream The input
     * @return The parsed object
     */
    protected RawJsonActions parseAsRawJsonActions(final InputStream stream) {
        return new Yaml().loadAs(stream, RawJsonActions.class);
    }

    /**
     * Converts an instance of {@link RawJsonActions} into a parsed object.
     *
     * @param rawJsonActions The RAW format
     * @return the converted actions.
     */
    protected JsonActions convertActions(final RawJsonActions rawJsonActions) {
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
