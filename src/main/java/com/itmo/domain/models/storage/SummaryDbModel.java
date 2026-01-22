package com.itmo.domain.models.storage;

import java.util.UUID;

public record SummaryDbModel(
        UUID id,
        String text
) {
}
