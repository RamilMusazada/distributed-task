package com.example.notification.listener;

import com.example.notification.event.ClientEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "notification-group")
    public void onClientEvent(ClientEvent event) {
        log.info("[NotificationService] Received event: id={}, client={}, action={}, createdAt={}",
                event.getEventId(), event.getClientName(), event.getAction(), event.getCreatedAt());
    }
}