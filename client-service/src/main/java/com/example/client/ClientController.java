package com.example.client;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    private final StorageClient storageClient;

    public ClientController(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    @GetMapping("/hello")
    public String hello() {
        return "client-service -> " + storageClient.hello();
    }

    @PostMapping("/items")
    public Map<String, Object> createItem(@RequestBody ItemRequest request) {
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
}
