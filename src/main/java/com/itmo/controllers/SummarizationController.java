package com.itmo.controllers;

import com.itmo.domain.services.summarization.SummarizationService;
import com.itmo.integration.requests.GetSummaryRequest;
import com.itmo.integration.responses.SummaryResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class SummarizationController {
    private final SummarizationService summarizationService;

    public SummarizationController(SummarizationService summarizationService) {
        this.summarizationService = summarizationService;
    }

    @PostMapping("/summary")
    public Mono<SummaryResponse> summary(@RequestBody GetSummaryRequest request) {
        var model = request.toModel();

        return summarizationService
                .summarize(model)
                .map(summary -> new SummaryResponse(summary.id(), summary.text()));
    }

    @GetMapping("/summary/{id}")
    public Mono<SummaryResponse> findById(@PathVariable("id") UUID id) {
        return summarizationService
                .findById(id)
                .map(summary -> new SummaryResponse(summary.id(), summary.text()));
    }
}
