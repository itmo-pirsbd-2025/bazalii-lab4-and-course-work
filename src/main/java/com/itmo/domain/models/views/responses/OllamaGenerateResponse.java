package com.itmo.domain.models.views.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OllamaGenerateResponse(
        String model,
        String response,
        Boolean done,

        @JsonProperty("total_duration")
        Long totalDuration,

        @JsonProperty("load_duration")
        Long loadDuration,

        @JsonProperty("prompt_eval_count")
        Integer promptEvalCount,

        @JsonProperty("eval_count")
        Integer evalCount
) {
}
