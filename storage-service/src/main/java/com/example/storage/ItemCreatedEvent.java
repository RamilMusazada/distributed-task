package com.example.storage;

import java.time.LocalDateTime;

public record ItemCreatedEvent(
        String eventId,
        Long itemId,
        String name,
        String description,
        LocalDateTime createdAt,
        String recipientEmail
) {
}