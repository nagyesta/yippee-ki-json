package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.JsonAction;
import com.github.nagyesta.yippeekijson.core.exception.JsonTransformException;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.JsonFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JsonTransformerImpl implements JsonTransformer {

    @Override
    public String transform(final InputStream json, final JsonAction action) throws JsonTransformException {
        Assert.notNull(json, "JSON stream cannot be null.");
        Assert.notNull(action, "Action cannot be null.");

        try {
            if (action.getRules().isEmpty()) {
                log.info("No rules found for action: " + action.getName() + ". Copying JSON without change.");
                return StreamUtils.copyToString(json, StandardCharsets.UTF_8);
            }

            final DocumentContext documentContext = JsonPath.parse(json);
            log.info("Parsed JSON document.");

            action.getRules().forEach(a -> a.accept(documentContext));

            return JsonFormatter.prettyPrint(documentContext.jsonString());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new JsonTransformException("failed to transform JSON document.", e);
        }
    }

    @Override
    public String transform(final File json, final JsonAction action) throws JsonTransformException {
        Assert.notNull(json, "JSON file cannot be null.");

        log.info("Processing file: " + json.getAbsolutePath() + " using action: " + action.getName());
        try (FileInputStream inputStream = new FileInputStream(json)) {
            return transform(inputStream, action);
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
            throw new JsonTransformException("IOException happened while processing JSON.", e);
        }
    }
}
