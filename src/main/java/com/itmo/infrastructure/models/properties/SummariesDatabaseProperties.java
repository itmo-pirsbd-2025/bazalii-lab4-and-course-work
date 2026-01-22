package com.itmo.infrastructure.models.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "summaries-db")
public record SummariesDatabaseProperties(
        String url,
        String username,
        String password
) {
}
