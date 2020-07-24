package com.github.nagyesta.yippeekijson.core.supplier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nagyesta.yippeekijson.core.annotation.EmbedParam;
import com.github.nagyesta.yippeekijson.core.annotation.NamedSupplier;
import com.github.nagyesta.yippeekijson.core.config.parser.FunctionRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.raw.RawConfigParam;
import com.github.nagyesta.yippeekijson.core.exception.AbortTransformationException;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.annotation.*;
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
    static final String PARAM_SOURCE = "source";

    private final Supplier<String> sourceSupplier;
    private final JsonMapper jsonMapper;

    @SchemaDefinition(
            outputType = JsonSchema.class,
            properties = @PropertyDefinitions({
                    @PropertyDefinition(name = PARAM_SOURCE,
                            type = @TypeDefinition(itemType = Supplier.class, itemTypeParams = String.class),
                            docs = "The supplier of the String of the JSON Schema which we will use as input."),
            }),
            wikiLink = @WikiLink(file = WikiConstants.BUILT_IN_SUPPLIERS, section = "JSON schema supplier"),
            sinceVersion = WikiConstants.VERSION_1_2_0,
            description = {
                    "This supplier returns a JSON schema from the string input provided in the configuration."
            },
            example = @Example(
                    in = "/examples/json/validation-input.json",
                    out = "/examples/json/validation-output.json",
                    yml = "/examples/yml/validation.yml",
                    note = "In this example we have provided a local file (which happens to be the input file) as schema.")
    )
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
