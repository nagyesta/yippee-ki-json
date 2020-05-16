package com.github.nagyesta.yippeekijson.core.config.parser.impl;

import com.github.nagyesta.yippeekijson.core.config.entities.JsonAction;
import com.github.nagyesta.yippeekijson.core.config.entities.JsonActions;
import com.github.nagyesta.yippeekijson.core.config.parser.ActionConfigParser;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonRuleRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonAction;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawJsonActions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.yaml.snakeyaml.Yaml;

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
    public YamlActionConfigParser(JsonRuleRegistry ruleRegistry) {
        Assert.notNull(ruleRegistry, "ruleRegistry cannot be null.");
        this.ruleRegistry = ruleRegistry;
    }

    @Override
    public JsonActions parse(InputStream stream) throws RuntimeException {
        Assert.notNull(stream, "stream cannot be null.");
        final RawJsonActions rawJsonActions = parseAsRawJsonActions(stream);

        final int parsedActions = Optional.ofNullable(rawJsonActions).map(RawJsonActions::getActions).map(List::size).orElse(0);
        log.info("Parsed " + parsedActions + " actions.");

        return convertActions(rawJsonActions);
    }

    RawJsonActions parseAsRawJsonActions(InputStream stream) {
        return new Yaml().loadAs(stream, RawJsonActions.class);
    }

    JsonActions convertActions(RawJsonActions rawJsonActions) {
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

    JsonAction convertSingleAction(RawJsonAction rawJsonAction) {
        if (rawJsonAction == null) {
            return null;
        }
        reindexRules(rawJsonAction);

        log.info("Converting " + rawJsonAction.getRules().size() + " rulse for action named: " + rawJsonAction.getName());
        final JsonAction.JsonActionBuilder actionBuilder = JsonAction.builder()
                .name(rawJsonAction.getName());

        rawJsonAction.getRules().stream()
                .map(ruleRegistry::newInstanceFrom)
                .forEach(actionBuilder::addRule);

        return actionBuilder.build();
    }

    void reindexRules(RawJsonAction a) {
        AtomicInteger index = new AtomicInteger(0);
        a.getRules().forEach(rule -> rule.setOrder(index.getAndIncrement()));
    }
}
