package com.hexagonal.demo.infrastructure.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "api.security")
public class ApiKeyProperties {
    private List<String> apiKeys;

    public List<String> getApiKeys() {
        return apiKeys == null ? null : List.copyOf(apiKeys);
    }

    public void setApiKeys(List<String> apiKeys) {
        this.apiKeys = apiKeys == null ? null : List.copyOf(apiKeys);
    }
}