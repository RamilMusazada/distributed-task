package com.example.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class StorageErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    public StorageErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = readMessage(response);
        if (response.status() >= 500) {
            return new StorageServerException(message);
        }
        return new StorageClientException(response.status(), message);
    }

    private String readMessage(Response response) {
        if (response.body() != null) {
            try (InputStream body = response.body().asInputStream()) {
                JsonNode json = objectMapper.readTree(body);
                if (json.hasNonNull("message")) {
                    return json.get("message").asText();
                }
            } catch (IOException ignored) {
            }
        }
        return "storage-service returned HTTP " + response.status();
    }
}