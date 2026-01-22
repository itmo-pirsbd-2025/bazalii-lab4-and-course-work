package com.itmo.domain.models.core;

import java.util.UUID;

public record Summary(
        UUID id,
        String text
) {
}