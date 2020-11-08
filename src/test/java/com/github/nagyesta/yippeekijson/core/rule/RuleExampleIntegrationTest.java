package com.github.nagyesta.yippeekijson.core.rule;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.yippeekijson.core.config.JsonRegistryConfig;
import com.github.nagyesta.yippeekijson.core.config.entities.JsonAction;
import com.github.nagyesta.yippeekijson.core.config.entities.JsonActions;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.YamlActionConfigParser;
import com.github.nagyesta.yippeekijson.core.control.JsonTransformer;
import com.github.nagyesta.yippeekijson.core.exception.ConfigParseException;
import com.github.nagyesta.yippeekijson.core.exception.JsonTransformException;
import com.github.nagyesta.yippeekijson.metadata.schema.WikiConstants;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaExporter;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentContext;
import com.github.nagyesta.yippeekijson.metadata.schema.entity.ComponentType;
import com.github.nagyesta.yippeekijson.metadata.schema.markdown.DocumentationExporter;
import com.github.nagyesta.yippeekijson.metadata.schema.parser.ComponentContextMetadataParser;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.nagyesta.yippeekijson.metadata.schema.markdown.impl.BaseMarkdownGenerator.*;
import static com.github.nagyesta.yippeekijson.test.helper.JsonTestUtil.jsonUtil;
import static net.steppschuh.markdowngenerator.Markdown.bold;

@LaunchAbortArmed
@SpringBootTest
@Slf4j
public class RuleExampleIntegrationTest {

    private static final String COMPONENT_SEPARATOR = "### ";
    private static final Predicate<String> BASE_PREDICATE = ((Predicate<String>) Objects::nonNull)
            .and(s -> Character.isUpperCase(s.charAt(0)))
            .and(s -> s.contains(bold(SECTION_NAME).toString()))
            .and(s -> s.contains(bold(SECTION_CLASS).toString()))
            .and(s -> s.contains(bold(SECTION_SINCE_VERSION).toString()))
            .and(s -> s.contains(SECTION_PARAMETERS))
            .and(s -> s.contains(SECTION_DESCRIPTION));
    private static final Predicate<String> LOOKS_LIKE_A_RULE = BASE_PREDICATE
            .and(s -> s.contains(SECTION_EXAMPLE_CONFIGURATION));
    private static final Predicate<String> LOOKS_LIKE_A_SUPPLIER = LOOKS_LIKE_A_RULE
            .and(s -> s.contains(bold(SECTION_OUTPUT_TYPE).toString()));
    private static final Predicate<String> LOOKS_LIKE_A_FUNCTION = BASE_PREDICATE
            .and(s -> s.contains(bold(SECTION_OUTPUT_TYPE).toString()))
            .and(s -> s.contains(bold(SECTION_INPUT_TYPE).toString()));
    private static final Predicate<String> LOOKS_LIKE_A_PREDICATE = LOOKS_LIKE_A_RULE
            .and(s -> s.contains(bold(SECTION_INPUT_TYPE).toString()));
    @Autowired
    private ComponentContextMetadataParser metadataParser;
    @Autowired
    private YamlActionConfigParser actionConfigParser;
    @Autowired
    private JsonTransformer jsonTransformer;
    @Autowired
    private DocumentationExporter documentationExporter;
    @Autowired
    private JsonSchemaExporter schemaExporter;

    private static Stream<Arguments> namedComponentProvider() {
        final JsonRegistryConfig registryConfig = new JsonRegistryConfig();
        List<Class<?>> result = new ArrayList<>();
        result.addAll(registryConfig.autoRegisterRules());
        result.addAll(registryConfig.autoRegisterSuppliers());
        result.addAll(registryConfig.autoRegisterPredicates());
        result.addAll(registryConfig.autoRegisterFunctions());
        return result.stream()
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("namedComponentProvider")
    void testComponentExampleIsValid(final Class<?> componentClass) throws ConfigParseException, IOException, JsonTransformException {
        //given
        log.info("Starting test of: " + componentClass.getName());
        final ComponentContext context = metadataParser.parse(componentClass.getDeclaredConstructors()[0]);
        if (context.getDocumentation().getExampleConfig().isEmpty()) {
            log.warn("Missing example: " + componentClass.getName());
            return;
        }
        final ClassPathResource configResource = context.getDocumentation().getExampleConfig().orElseThrow();
        final JsonActions actions = actionConfigParser.parse(configResource.getInputStream(), false);
        final ClassPathResource inputResource = context.getDocumentation().getExampleInput().orElseThrow();
        final ClassPathResource outputResource = context.getDocumentation().getExampleOutput().orElseThrow();
        final String output = IOUtils.toString(outputResource.getInputStream(), StandardCharsets.UTF_8);
        final JsonAction action = actions.getActions().get("demo");

        if (context.getDocumentation().isSkipTest()) {
            log.info("Skipped test of: " + componentClass.getName());
            return;
        }

        //when
        final String result = jsonTransformer.transform(inputResource.getInputStream(), StandardCharsets.UTF_8, action);

        //then
        final JsonNode expected = jsonUtil().readAsTree(output);
        final JsonNode actual = jsonUtil().readAsTree(result);
        Assertions.assertEquals(expected, actual);
        log.info("Completed test of: " + componentClass.getName());
    }

    @ParameterizedTest
    @MethodSource("namedComponentProvider")
    void testComponentExampleYmlIsValid(final Class<?> componentClass) throws ConfigParseException, IOException {
        //given
        log.info("Starting test of: " + componentClass.getName());
        final ComponentContext context = metadataParser.parse(componentClass.getDeclaredConstructors()[0]);
        if (context.getDocumentation().getExampleConfig().isEmpty()) {
            log.warn("Missing example: " + componentClass.getName());
            return;
        }
        final ClassPathResource configResource = context.getDocumentation().getExampleConfig().orElseThrow();
        final JsonSchema schema = parseYamlSchema();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        try (InputStream inputStream = configResource.getInputStream()) {
            JsonNode jsonNode = mapper.readTree(inputStream);

            //when
            Set<ValidationMessage> violations = schema.validate(jsonNode);

            //then
            Assertions.assertEquals(Set.of(), violations);
        }
        log.info("Completed test of: " + componentClass.getName());
    }

    @Test
    void testRuleMarkdownGenerationIsWorking() throws IOException {
        //given

        //when
        final Map<String, String> actual = documentationExporter.exportDocumentation(ComponentType.RULE);

        //then
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(Set.of(WikiConstants.BUILT_IN_RULES), actual.keySet());
        final String actualMarkdown = actual.get(WikiConstants.BUILT_IN_RULES);
        Assertions.assertFalse(actualMarkdown.contains(MISSING_DOCUMENTATION));
        final List<String> notMatching = Arrays.stream(actualMarkdown.split(COMPONENT_SEPARATOR))
                .skip(1)
                .filter(Predicate.not(LOOKS_LIKE_A_RULE))
                .collect(Collectors.toList());
        Assertions.assertEquals(List.of(), notMatching);

    }

    @Test
    void testSupplierMarkdownGenerationIsWorking() throws IOException {
        //given

        //when
        final Map<String, String> actual = documentationExporter.exportDocumentation(ComponentType.SUPPLIER);

        //then
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(Set.of(WikiConstants.BUILT_IN_SUPPLIERS), actual.keySet());
        final String actualMarkdown = actual.get(WikiConstants.BUILT_IN_SUPPLIERS);
        Assertions.assertFalse(actualMarkdown.contains(MISSING_DOCUMENTATION));
        final List<String> notMatching = Arrays.stream(actualMarkdown.split(COMPONENT_SEPARATOR))
                .skip(1)
                .filter(Predicate.not(LOOKS_LIKE_A_SUPPLIER))
                .collect(Collectors.toList());
        Assertions.assertEquals(List.of(), notMatching);

    }

    @Test
    void testPredicateMarkdownGenerationIsWorking() throws IOException {
        //given

        //when
        final Map<String, String> actual = documentationExporter.exportDocumentation(ComponentType.PREDICATE);

        //then
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(Set.of(WikiConstants.BUILT_IN_PREDICATES), actual.keySet());
        final String actualMarkdown = actual.get(WikiConstants.BUILT_IN_PREDICATES);
        Assertions.assertFalse(actualMarkdown.contains(MISSING_DOCUMENTATION));
        final List<String> notMatching = Arrays.stream(actualMarkdown.split(COMPONENT_SEPARATOR))
                .skip(1)
                .filter(Predicate.not(LOOKS_LIKE_A_PREDICATE))
                .collect(Collectors.toList());
        Assertions.assertEquals(List.of(), notMatching);
    }

    @Test
    void testFunctionMarkdownGenerationIsWorking() throws IOException {
        //given

        //when
        final Map<String, String> actual = documentationExporter.exportDocumentation(ComponentType.FUNCTION);

        //then
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(Set.of(WikiConstants.BUILT_IN_FUNCTIONS), actual.keySet());
        final String actualMarkdown = actual.get(WikiConstants.BUILT_IN_FUNCTIONS);
        Assertions.assertFalse(actualMarkdown.contains(MISSING_DOCUMENTATION));
        final List<String> notMatching = Arrays.stream(actualMarkdown.split(COMPONENT_SEPARATOR))
                .skip(1)
                .filter(Predicate.not(LOOKS_LIKE_A_FUNCTION))
                .collect(Collectors.toList());
        Assertions.assertEquals(List.of(), notMatching);

    }


    private JsonSchema parseYamlSchema() throws ConfigParseException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        JsonSchemaFactory factory = JsonSchemaFactory
                .builder(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7))
                .objectMapper(mapper)
                .build();

        try {
            return factory.getSchema(this.schemaExporter.exportSchema());
        } catch (final IOException e) {
            throw new ConfigParseException(e.getMessage(), e);
        }
    }
}
