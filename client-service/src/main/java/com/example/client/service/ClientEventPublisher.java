package com.example.client.service;

import com.example.client.event.ClientEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClientEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ClientEventPublisher.class);

    private final KafkaTemplate<String, ClientEvent> kafkaTemplate;
    private final String topic;

    public ClientEventPublisher(KafkaTemplate<String, ClientEvent> kafkaTemplate,
                                @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(ClientEvent event) {
        kafkaTemplate.send(topic, event.getEventId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event {}", event.getEventId(), ex);
                    } else {
                        log.info("[ClientService] Published event {} to topic={} partition={} offset={}",
                                event.getEventId(), topic,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}