package com.github.nagyesta.yippeekijson.core.config;

import com.github.nagyesta.yippeekijson.core.config.parser.ActionConfigParser;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonMapper;
import com.github.nagyesta.yippeekijson.core.config.parser.JsonRuleRegistry;
import com.github.nagyesta.yippeekijson.core.config.parser.impl.YamlActionConfigParser;
import com.github.nagyesta.yippeekijson.core.control.*;
import com.github.nagyesta.yippeekijson.metadata.schema.definitions.JsonSchemaExporter;
import com.github.nagyesta.yippeekijson.metadata.schema.markdown.DocumentationExporter;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ControllerConfig {

    @Autowired
    private JsonMapper yippeeJsonMapper;
    @Autowired
    private Validator validator;
    @Autowired
    private JsonRuleRegistry jsonRuleRegistry;
    @Autowired
    private DocumentationExporter documentExporter;
    @Autowired
    private JsonSchemaExporter jsonSchemaExporter;

    @Bean
    public ActionConfigParser actionConfigParser() {
        return new YamlActionConfigParser(jsonRuleRegistry, validator);
    }

    @Bean
    public FileSetTransformer fileSetTransformer() {
        return new FileSetTransformerImpl();
    }

    @Bean
    public JsonTransformer jsonTransformer() {
        return new JsonTransformerImpl(yippeeJsonMapper);
    }

    @Bean
    public ApplicationController filePairProcessorController() {
        return new FilePairProcessorController(jsonTransformer(), fileSetTransformer(), actionConfigParser(), validator);
    }

    @Bean
    public ApplicationController markdownExportController() {
        return new MarkdownExportController(documentExporter, validator);
    }

    @Bean
    public ApplicationController yamlSchemaExportController() {
        return new YamlSchemaExportController(jsonSchemaExporter, validator);
    }

    @Bean
    @Primary
    public ApplicationController dispatcherApplicationController() {
        return new DispatcherApplicationController(
                filePairProcessorController(),
                markdownExportController(),
                yamlSchemaExportController());
    }

}
