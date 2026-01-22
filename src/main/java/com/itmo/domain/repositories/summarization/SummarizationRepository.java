package com.itmo.domain.repositories.summarization;

import com.itmo.domain.models.core.Summary;
import com.itmo.domain.models.storage.SummaryDbModel;
import com.itmo.domain.models.views.requests.OllamaGenerateRequest;
import com.itmo.domain.models.views.responses.OllamaGenerateResponse;
import com.itmo.domain.serviceClients.ollama.OllamaClient;

import com.itmo.domain.storage.SummariesDatabase;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class SummarizationRepository {

    private final String model = "llama3.1:8b";

    private final OllamaClient ollamaClient;
    private final SummariesDatabase summariesDatabase;

    public SummarizationRepository(OllamaClient ollamaClient, SummariesDatabase summariesDatabase) {
        this.ollamaClient = ollamaClient;
        this.summariesDatabase = summariesDatabase;
    }

    public Mono<String> summarize(String prompt) {
        var request = new OllamaGenerateRequest(model, prompt);
        return ollamaClient
                .generate(request)
                .map(OllamaGenerateResponse::response);
    }

    public Mono<Void> saveSummary(Summary summary) {
        var dbModel = new SummaryDbModel(summary.id(), summary.text());

        return summariesDatabase.save(dbModel);
    }

    public Mono<Summary> findById(UUID id) {
        return summariesDatabase
                .findById(id)
                .map(dbModel -> new Summary(dbModel.id(), dbModel.text()));
    }
}
