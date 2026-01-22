package com.itmo.integration.requests;

public record MessageRequest(
        String author,
        String content
) {
}
