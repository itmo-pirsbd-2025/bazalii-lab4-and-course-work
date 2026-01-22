package com.itmo.domain.serviceClients.ollama;

import com.itmo.domain.models.views.requests.OllamaGenerateRequest;
import com.itmo.domain.models.views.responses.OllamaGenerateResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class OllamaClient {

    private final WebClient ollamaWebClient;

    public OllamaClient(WebClient ollamaWebClient) {
        this.ollamaWebClient = ollamaWebClient;
    }

    public Mono<OllamaGenerateResponse> generate(OllamaGenerateRequest request) {
        return ollamaWebClient
                .post()
                .uri("/api/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OllamaGenerateResponse.class)
                .timeout(Duration.ofSeconds(120));
    }
}
