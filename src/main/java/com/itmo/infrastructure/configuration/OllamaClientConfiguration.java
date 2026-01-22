package com.itmo.infrastructure.configuration;

import com.itmo.infrastructure.models.exceptions.OllamaException;
import com.itmo.infrastructure.models.properties.OllamaClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@EnableConfigurationProperties(OllamaClientProperties.class)
public class OllamaClientConfiguration {

    @Bean
    public WebClient ollamaWebClient(OllamaClientProperties props) {
        return WebClient
                .builder()
                .baseUrl(props.baseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(mapErrors())
                .build();
    }

    private static ExchangeFilterFunction mapErrors() {
        return ExchangeFilterFunction.ofResponseProcessor(resp -> {
            if (resp.statusCode().is2xxSuccessful()) {
                return Mono.just(resp);
            }

            return resp
                    .bodyToMono(String.class)
                    .defaultIfEmpty("")
                    .flatMap(body -> Mono.error(new OllamaException(
                            "Ollama HTTP " + resp.statusCode() + (body.isBlank() ? "" : (" | body: " + body)),
                            resp.statusCode().value()
                    )));
        });
    }
}
