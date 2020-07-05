package com.github.nagyesta.yippeekijson.core.control;

import com.github.nagyesta.yippeekijson.core.config.entities.JsonAction;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.exception.JsonTransformException;
import com.github.nagyesta.yippeekijson.core.exception.StopRuleProcessingException;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.JsonFormatter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

@Slf4j
public class JsonTransformerImpl implements JsonTransformer {

    private final JsonMapper mapper;

    public JsonTransformerImpl(@NonNull final JsonMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String transform(@NonNull final InputStream json,
                            @NonNull final Charset charset,
                            @NonNull final JsonAction action) throws JsonTransformException {
        try {
            if (action.getRules().isEmpty()) {
                log.info("No rules found for action: " + action.getName() + ". Copying JSON without change.");
                return StreamUtils.copyToString(json, charset);
            }
            final Configuration configuration = mapper.parserConfiguration();
            final DocumentContext documentContext = JsonPath.parse(json, configuration);
            log.info("Parsed JSON document.");

            try {
                action.getRules().forEach(rule -> rule.accept(documentContext));
            } catch (final StopRuleProcessingException e) {
                log.error("Rule processing is stopped: " + e.getMessage());
            }
            return JsonFormatter.prettyPrint(documentContext.jsonString());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new JsonTransformException("failed to transform JSON document.", e);
        }
    }

    @Override
    public String transform(@NonNull final File json,
                            @NonNull final Charset charset,
                            @NonNull final JsonAction action) throws JsonTransformException {
        log.info("Processing file: " + json.getAbsolutePath() + " using action: " + action.getName());
        try (FileInputStream inputStream = new FileInputStream(json)) {
            return transform(inputStream, charset, action);
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
            throw new JsonTransformException("IOException happened while processing JSON.", e);
        }
    }
}
