package com.example.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "storage-client", url = "${storage.service.url}")
public interface StorageClient {

    @GetMapping("/api/storage/hello")
    String hello();

    @PostMapping("/api/storage/items")
    Map<String, Object> saveItem(@RequestBody ItemRequest request);

    @GetMapping("/api/storage/items")
    List<Map<String, Object>> getItems();
}
