package com.itmo.infrastructure.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Summarization API",
                description = "API for message thread summarization",
                version = "1.0.0"
        )
)
public class OpenApiDefinitionConfig {
}
