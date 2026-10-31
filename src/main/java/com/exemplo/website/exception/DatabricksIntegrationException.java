package com.exemplo.website.exception;

public class DatabricksIntegrationException extends RuntimeException {

    public DatabricksIntegrationException(String message) {
        super(message);
    }

    public DatabricksIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}