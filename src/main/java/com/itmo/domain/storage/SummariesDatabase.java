package com.itmo.domain.storage;

import com.itmo.domain.models.storage.SummaryDbModel;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class SummariesDatabase {

    private final DatabaseClient databaseClient;

    public SummariesDatabase(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<Void> save(SummaryDbModel model) {
        return databaseClient
                .sql("insert into summaries (id, text) values (:id, :text)")
                .bind("id", model.id())
                .bind("text", model.text())
                .then();
    }

    public Mono<SummaryDbModel> findById(UUID id) {
        return databaseClient
                .sql("select id, text from summaries where id = :id")
                .bind("id", id)
                .map(row -> {
                    var summaryId = row.get("id", UUID.class);
                    var text = row.get("text", String.class);

                    return new SummaryDbModel(summaryId, text);
                })
                .one();
    }
}
