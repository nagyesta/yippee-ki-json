package com.github.nagyesta.yippeekijson.core.config;

import com.github.nagyesta.yippeekijson.core.config.validation.FileValidator;
import com.github.nagyesta.yippeekijson.core.config.validation.YippeeConfigValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {

    @Bean
    public FileValidator configValidator() {
        return new FileValidator("config", true, true, null, false);
    }

    @Bean
    public FileValidator inputValidator() {
        return new FileValidator("input", true, true, null, null);
    }

    @Bean
    public FileValidator outputFileValidator() {
        return new FileValidator("output", true, null, true, false);
    }

    @Bean
    public FileValidator outputDirectoryValidator() {
        return new FileValidator("outputDirectory", true, null, true, true);
    }

    @Bean
    public YippeeConfigValidator yippeeConfigValidator() {
        return new YippeeConfigValidator(configValidator(), inputValidator(), outputFileValidator(), outputDirectoryValidator());
    }
}
