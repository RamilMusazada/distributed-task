package com.example.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client")
@Retry(name = "storage")
@CircuitBreaker(name = "storage")
public class ClientController {

    private final StorageClient storageClient;

    public ClientController(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    @GetMapping("/hello")
    public String hello() {
        return storageClient.hello();
    }

    @PostMapping("/items")
    public Map<String, Object> createItem(@RequestBody ItemRequest request) {
        if (request.name() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        ItemRequest processed = new ItemRequest(
                request.name().trim().toUpperCase(),
                request.description() == null ? "" : request.description().trim()
        );
        return storageClient.saveItem(processed);
    }

    @GetMapping("/items")
    public List<Map<String, Object>> getItems() {
        return storageClient.getItems();
    }

    @GetMapping("/items/{id}")
    public Map<String, Object> getItem(@PathVariable Long id) {
        return storageClient.getItem(id);
    }

    @GetMapping("/fail/{status}")
    public String fail(@PathVariable int status) {
        return storageClient.error(status);
    }
}