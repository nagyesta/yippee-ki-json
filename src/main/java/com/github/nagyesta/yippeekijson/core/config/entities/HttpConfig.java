package com.github.nagyesta.yippeekijson.core.config.entities;

import com.github.nagyesta.yippeekijson.core.annotation.Injectable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Injectable(forType = HttpConfig.class)
@Configuration
@ConfigurationProperties(prefix = "additional.http", ignoreUnknownFields = false)
public class HttpConfig {
    private String userAgent;
    private boolean addDefaultHeaders;
    private int minSuccessStatus;
    private int maxSuccessStatus;
    private int timeoutSeconds;

    public HttpConfig() {
    }

    private HttpConfig(final HttpConfigBuilder builder) {
        this.userAgent = builder.userAgent;
        this.addDefaultHeaders = builder.addDefaultHeaders;
        this.minSuccessStatus = builder.minSuccessStatus;
        this.maxSuccessStatus = builder.maxSuccessStatus;
        this.timeoutSeconds = builder.timeoutSeconds;
    }

    public static HttpConfigBuilder builder() {
        return new HttpConfigBuilder();
    }

    @SuppressWarnings({"UnusedReturnValue", "checkstyle:HiddenField", "checkstyle:DesignForExtension"})
    public static class HttpConfigBuilder {
        private String userAgent;
        private boolean addDefaultHeaders;
        private int minSuccessStatus;
        private int maxSuccessStatus;
        private int timeoutSeconds;

        HttpConfigBuilder() {
        }

        public HttpConfigBuilder userAgent(final String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public HttpConfigBuilder addDefaultHeaders(final boolean addDefaultHeaders) {
            this.addDefaultHeaders = addDefaultHeaders;
            return this;
        }

        public HttpConfigBuilder minSuccessStatus(final int minSuccessStatus) {
            this.minSuccessStatus = minSuccessStatus;
            return this;
        }

        public HttpConfigBuilder maxSuccessStatus(final int maxSuccessStatus) {
            this.maxSuccessStatus = maxSuccessStatus;
            return this;
        }

        public HttpConfigBuilder timeoutSeconds(final int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            return this;
        }

        public HttpConfig build() {
            return new HttpConfig(this);
        }

    }
}
