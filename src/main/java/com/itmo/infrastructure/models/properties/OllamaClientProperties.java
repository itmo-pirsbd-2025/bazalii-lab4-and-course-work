package com.itmo.infrastructure.models.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "ollama-client")
public record OllamaClientProperties(
        String baseUrl,
        Duration requestTimeout
) {
}
