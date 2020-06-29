package com.github.nagyesta.yippeekijson.core.config.entities;

import com.github.nagyesta.yippeekijson.core.annotation.Injectable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Injectable(forType = SchemaStoreConfig.class)
@Configuration
@ConfigurationProperties(prefix = "additional.schema-store", ignoreUnknownFields = false)
public class SchemaStoreConfig {
    private String catalogUri;
    private String mappingNameKey;
    private String mappingUrlKey;
    private String schemaArrayPath;

    public SchemaStoreConfig() {
    }

    private SchemaStoreConfig(final SchemaStoreConfigBuilder builder) {
        this.catalogUri = builder.catalogUri;
        this.mappingNameKey = builder.mappingNameKey;
        this.mappingUrlKey = builder.mappingUrlKey;
        this.schemaArrayPath = builder.schemaArrayPath;
    }

    public static SchemaStoreConfigBuilder builder() {
        return new SchemaStoreConfigBuilder();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static class SchemaStoreConfigBuilder {
        private String catalogUri;
        private String mappingNameKey;
        private String mappingUrlKey;
        private String schemaArrayPath;

        SchemaStoreConfigBuilder() {
        }

        public SchemaStoreConfigBuilder catalogUri(final String catalogUri) {
            this.catalogUri = catalogUri;
            return this;
        }

        public SchemaStoreConfigBuilder mappingNameKey(final String mappingNameKey) {
            this.mappingNameKey = mappingNameKey;
            return this;
        }

        public SchemaStoreConfigBuilder schemaArrayPath(final String schemaArrayPath) {
            this.schemaArrayPath = schemaArrayPath;
            return this;
        }

        public SchemaStoreConfigBuilder mappingUrlKey(final String mappingUrlKey) {
            this.mappingUrlKey = mappingUrlKey;
            return this;
        }

        public SchemaStoreConfig build() {
            return new SchemaStoreConfig(this);
        }
    }
}
