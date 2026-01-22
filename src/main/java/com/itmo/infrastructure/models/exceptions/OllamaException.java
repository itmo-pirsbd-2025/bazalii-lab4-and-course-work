package com.itmo.infrastructure.models.exceptions;

public class OllamaException extends RuntimeException {
    private final int statusCode;

    public OllamaException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
