package com.example.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String from;
    private final String defaultRecipient;

    public EmailService(JavaMailSender mailSender,
                        @Value("${notification.from:}") String from,
                        @Value("${notification.default-recipient:}") String defaultRecipient) {
        this.mailSender = mailSender;
        this.from = from;
        this.defaultRecipient = defaultRecipient;
    }

    public void sendItemCreatedEmail(ItemCreatedEvent event) {
        String to = isBlank(event.recipientEmail()) ? defaultRecipient : event.recipientEmail();
        if (isBlank(to)) {
            throw new IllegalStateException("No recipient for eventId=" + event.eventId());
        }

        SimpleMailMessage message = new SimpleMailMessage();
        if (!isBlank(from)) {
            message.setFrom(from);
        }
        message.setTo(to);
        message.setSubject("New item created: " + event.name());
        message.setText("""
                A new item has been created.

                ID: %s
                Name: %s
                Description: %s
                Created at: %s
                """.formatted(event.itemId(), event.name(), event.description(), event.createdAt()));

        mailSender.send(message);
        log.info("Email sent to {} for itemId={}", to, event.itemId());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}