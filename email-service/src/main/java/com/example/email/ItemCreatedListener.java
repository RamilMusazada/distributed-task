package com.example.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ItemCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(ItemCreatedListener.class);

    private final EmailService emailService;

    public ItemCreatedListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void onItemCreated(ItemCreatedEvent event) {
        log.info("Received ItemCreatedEvent eventId={} itemId={}", event.eventId(), event.itemId());
        emailService.sendItemCreatedEmail(event);
    }
}