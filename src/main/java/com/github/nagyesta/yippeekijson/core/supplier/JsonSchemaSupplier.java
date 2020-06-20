package com.github.nagyesta.yippeekijson.core.supplier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nagyesta.yippeekijson.core.annotation.EmbedParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import com.github.nagyesta.yippeekijson.core.exception.AbortTransformationException;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Supplier;

@Slf4j
public class JsonSchemaSupplier implements Supplier<JsonSchema> {

    static final String NAME = "jsonSchema";

    private final Supplier<String> sourceSupplier;
    private final JsonMapper jsonMapper;

    @NamedSupplier(NAME)
    public JsonSchemaSupplier(@EmbedParam @NonNull final Map<String, RawConfigParam> source,
                              @NonNull final JsonMapper jsonMapper,
                              @NonNull final FunctionRegistry functionRegistry) {
        this.jsonMapper = jsonMapper;
        this.sourceSupplier = functionRegistry.lookupSupplier(source);
    }

    @Override
    public JsonSchema get() {
        final ObjectMapper objectMapper = jsonMapper.objectMapper();
        try {
            final JsonNode schemaNode = objectMapper.readTree(sourceSupplier.get());
            JsonSchemaFactory factory = JsonSchemaFactory
                    .builder(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7))
                    .objectMapper(objectMapper)
                    .build();
            return factory.getSchema(schemaNode);
        } catch (final Exception e) {
            log.error("Failed to supply schema: " + e.getMessage(), e);
            throw new AbortTransformationException("Failed to supply schema: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JsonSchemaSupplier.class.getSimpleName() + "[", "]")
                .add("sourceSupplier=" + sourceSupplier)
                .toString();
    }
}
