package com.itmo.integration.responses;

import java.util.UUID;

public record SummaryResponse(
        UUID id,
        String text
) {
}
