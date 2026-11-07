package com.example.notification.event;

public class ClientEvent {

    private String eventId;
    private String clientName;
    private String action;
    private String createdAt;

    public ClientEvent() {
    }

    public ClientEvent(String eventId, String clientName, String action, String createdAt) {
        this.eventId = eventId;
        this.clientName = clientName;
        this.action = action;
        this.createdAt = createdAt;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}