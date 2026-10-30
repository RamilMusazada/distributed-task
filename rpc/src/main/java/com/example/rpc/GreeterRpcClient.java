package com.example.rpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;


public class GreeterRpcClient {

    public static final Duration TIMEOUT = Duration.ofSeconds(2);

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    private final AtomicInteger nextId = new AtomicInteger(1);
    private final URI endpoint;

    public GreeterRpcClient(String host, int port) {
        this.endpoint = URI.create("http://" + host + ":" + port + "/rpc");
    }


    public String sayHello(String name) throws IOException, InterruptedException {
        ObjectNode request = mapper.createObjectNode();
        request.put("jsonrpc", "2.0");
        request.put("method", "sayHello");
        request.putObject("params").put("name", name);
        request.put("id", nextId.getAndIncrement());

        HttpRequest httpRequest = HttpRequest.newBuilder(endpoint)
                .timeout(TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(request)))
                .build();

        HttpResponse<String> response = http.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Unexpected HTTP status " + response.statusCode());
        }

        JsonNode body = mapper.readTree(response.body());
        JsonNode error = body.get("error");
        if (error != null && !error.isNull()) {
            throw new JsonRpcException(error.path("code").asInt(), error.path("message").asText());
        }
        JsonNode result = body.get("result");
        if (result == null || !result.isTextual()) {
            throw new IOException("Malformed JSON-RPC response: " + response.body());
        }
        return result.asText();
    }

    public static void main(String[] args) throws Exception {
        String name = args.length > 0 ? args[0] : "Ada";
        String host = args.length > 1 ? args[1] : "localhost";
        int port = args.length > 2 ? Integer.parseInt(args[2]) : GreeterRpcServer.DEFAULT_PORT;

        try {
            System.out.println(new GreeterRpcClient(host, port).sayHello(name));
        } catch (JsonRpcException e) {
            System.err.println("JSON-RPC error " + e.getCode() + ": " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Request failed: " + e);
            System.exit(1);
        }
    }
}