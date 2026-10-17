package com.example.client;

import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StorageClientException.class)
    public ResponseEntity<Map<String, Object>> storageClientError(StorageClientException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(body(ex.getStatus(), ex.getMessage(), "storage-service"));
    }

    @ExceptionHandler(StorageServerException.class)
    public ResponseEntity<Map<String, Object>> storageServerError(StorageServerException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(body(502, "storage-service failed: " + ex.getMessage(), "storage-service"));
    }

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<Map<String, Object>> storageUnreachable(RetryableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(body(503, "storage-service is unreachable", "client-service"));
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<Map<String, Object>> circuitOpen(CallNotPermittedException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(body(503, "Circuit breaker is OPEN: storage-service calls are blocked for a while", "client-service"));
    }

    private Map<String, Object> body(int status, String message, String source) {
        return Map.of("status", status, "message", message, "source", source);
    }
}