package com.github.nagyesta.yippeekijson.core.config.entities;

import com.github.nagyesta.yippeekijson.core.annotation.Injectable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Injectable(forType = AdditionalConfig.class)
@Configuration
@ConfigurationProperties(prefix = "additional", ignoreUnknownFields = false)
public class AdditionalConfig {
    private SchemaStoreConfig schemaStore;
    private HttpConfig http;
}
