package com.example.storage.listener;

import com.example.storage.event.ClientEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ClientEventListener {

    private static final Logger log = LoggerFactory.getLogger(ClientEventListener.class);

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "storage-group")
    public void onClientEvent(ClientEvent event) {
        log.info("[StorageService] Received event: id={}, client={}, action={}, createdAt={}",
                event.getEventId(), event.getClientName(), event.getAction(), event.getCreatedAt());
    }
}