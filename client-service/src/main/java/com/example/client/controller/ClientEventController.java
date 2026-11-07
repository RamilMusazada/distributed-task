package com.example.client.controller;

import com.example.client.event.ClientEvent;
import com.example.client.service.ClientEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/client")
public class ClientEventController {

    private final ClientEventPublisher publisher;

    public ClientEventController(ClientEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping("/events")
    public ResponseEntity<Map<String, String>> publish(@RequestBody Map<String, String> body) {
        ClientEvent event = new ClientEvent(
                UUID.randomUUID().toString(),
                body.getOrDefault("clientName", "unknown"),
                body.getOrDefault("action", "CREATED"),
                Instant.now().toString());
        publisher.publish(event);
        return ResponseEntity.accepted().body(Map.of("eventId", event.getEventId()));
    }
}