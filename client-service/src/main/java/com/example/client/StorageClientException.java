package com.example.client;

public class StorageClientException extends RuntimeException {

    private final int status;

    public StorageClientException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}