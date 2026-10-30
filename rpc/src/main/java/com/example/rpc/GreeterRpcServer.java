package com.example.rpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class GreeterRpcServer {

    public static final int DEFAULT_PORT = 8090;
    private static final int MAX_BODY_BYTES = 64 * 1024;

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpServer httpServer;
    private final ExecutorService executor = Executors.newFixedThreadPool(8);

    public GreeterRpcServer(int port) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        this.httpServer.createContext("/rpc", this::handle);
        this.httpServer.setExecutor(executor);
    }

    public GreeterRpcServer start() {
        httpServer.start();
        return this;
    }

    public int getPort() {
        return httpServer.getAddress().getPort();
    }

    public void stop() {
        httpServer.stop(0);
        executor.shutdownNow();
    }

    private void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().add("Allow", "POST");
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            ObjectNode response = process(readBody(exchange.getRequestBody()));
            byte[] bytes = mapper.writeValueAsBytes(response);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        }
    }

    private static String readBody(InputStream in) throws IOException {
        byte[] bytes = in.readNBytes(MAX_BODY_BYTES + 1);
        if (bytes.length > MAX_BODY_BYTES) {
            throw new IOException("request body too large");
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }


    ObjectNode process(String body) {
        JsonNode request;
        try {
            request = mapper.readTree(body);
        } catch (IOException e) {
            return error(null, JsonRpcException.PARSE_ERROR, "Parse error");
        }
        if (request == null || !request.isObject()) {
            return error(null, JsonRpcException.INVALID_REQUEST, "Invalid Request");
        }

        JsonNode id = request.get("id");
        JsonNode method = request.get("method");
        if (method == null || !method.isTextual()) {
            return error(id, JsonRpcException.INVALID_REQUEST, "Invalid Request: method is required");
        }
        if (!"sayHello".equals(method.asText())) {
            return error(id, JsonRpcException.METHOD_NOT_FOUND, "Method not found: " + method.asText());
        }

        String name = extractName(request.get("params"));
        if (name == null || name.isBlank()) {
            return error(id, JsonRpcException.INVALID_PARAMS, "name must not be empty");
        }

        ObjectNode ok = mapper.createObjectNode();
        ok.put("jsonrpc", "2.0");
        ok.put("result", "Hello, " + name);
        ok.set("id", id == null ? mapper.nullNode() : id);
        return ok;
    }


    private static String extractName(JsonNode params) {
        if (params == null) {
            return null;
        }
        JsonNode value = null;
        if (params.isObject()) {
            value = params.get("name");
        } else if (params.isArray() && params.size() > 0) {
            value = params.get(0);
        }
        return value != null && value.isTextual() ? value.asText() : null;
    }

    private ObjectNode error(JsonNode id, int code, String message) {
        ObjectNode response = mapper.createObjectNode();
        response.put("jsonrpc", "2.0");
        ObjectNode err = response.putObject("error");
        err.put("code", code);
        err.put("message", message);
        response.set("id", id == null ? mapper.nullNode() : id);
        return response;
    }

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        GreeterRpcServer server = new GreeterRpcServer(port).start();
        System.out.println("JSON-RPC Greeter server listening on http://localhost:" + server.getPort() + "/rpc");
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }
}