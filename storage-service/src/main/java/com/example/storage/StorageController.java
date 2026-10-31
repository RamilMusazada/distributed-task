package com.example.storage;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    private final ItemRepository repository;
    private final ItemEventPublisher eventPublisher;

    public StorageController(ItemRepository repository, ItemEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from storage-service";
    }

    @PostMapping("/items")
    public Item save(@RequestBody ItemRequest request) {
        Item saved = repository.save(new Item(request.name(), request.description()));
        eventPublisher.publishItemCreated(saved);
        return saved;
    }

    @GetMapping("/items")
    public List<Item> findAll() {
        return repository.findAll();
    }

    @GetMapping("/items/{id}")
    public Item findById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item " + id + " not found"));
    }

    @GetMapping("/error/{status}")
    public String error(@PathVariable int status) {
        throw new ResponseStatusException(HttpStatusCode.valueOf(status), "Simulated error " + status);
    }
}