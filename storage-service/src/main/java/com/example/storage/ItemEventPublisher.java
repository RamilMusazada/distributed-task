package com.example.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ItemEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ItemEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String recipientEmail;

    public ItemEventPublisher(RabbitTemplate rabbitTemplate,
                              @Value("${notification.recipient-email:}") String recipientEmail) {
        this.rabbitTemplate = rabbitTemplate;
        this.recipientEmail = recipientEmail;
    }

    public void publishItemCreated(Item item) {
        ItemCreatedEvent event = new ItemCreatedEvent(
                UUID.randomUUID().toString(),
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getCreatedAt(),
                recipientEmail
        );
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, event);
            log.info("Published ItemCreatedEvent eventId={} itemId={}", event.eventId(), event.itemId());
        } catch (AmqpException ex) {
            log.error("Failed to publish ItemCreatedEvent for itemId={}", event.itemId(), ex);
        }
    }
}