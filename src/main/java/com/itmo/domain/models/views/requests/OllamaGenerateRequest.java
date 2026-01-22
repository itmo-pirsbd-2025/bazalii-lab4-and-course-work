package com.itmo.domain.models.views.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record OllamaGenerateRequest(
        String model,
        String prompt,
        Boolean stream,
        Double temperature,
        @JsonProperty("top_p")
        Double topP,
        Map<String, Object> options
) {
    public OllamaGenerateRequest(String model, String prompt) {
        this(model, prompt, false, 0.7, 0.9, Map.of());
    }
}
