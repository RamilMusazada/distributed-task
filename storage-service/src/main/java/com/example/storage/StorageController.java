package com.example.storage;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    private final ItemRepository repository;

    public StorageController(ItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from storage-service";
    }

    @PostMapping("/items")
    public Item save(@RequestBody ItemRequest request) {
        return repository.save(new Item(request.name(), request.description()));
    }

    @GetMapping("/items")
    public List<Item> findAll() {
        return repository.findAll();
    }
}
