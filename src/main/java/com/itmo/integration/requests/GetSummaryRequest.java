package com.itmo.integration.requests;

import com.itmo.domain.models.core.Message;
import com.itmo.domain.models.core.SummaryCreationModel;

import java.util.Arrays;

public record GetSummaryRequest(
        MessageRequest[] messages
) {
    public SummaryCreationModel toModel() {
        return new SummaryCreationModel(
                Arrays
                        .stream(messages)
                        .map(request -> new Message(request.author(), request.content()))
                        .toArray(Message[]::new)
        );
    }
}
