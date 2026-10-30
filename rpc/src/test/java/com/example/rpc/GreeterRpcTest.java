package com.example.rpc;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GreeterRpcTest {

    private static GreeterRpcServer server;
    private static GreeterRpcClient client;

    @BeforeAll
    static void start() throws Exception {
        server = new GreeterRpcServer(0).start(); // port 0 = any free port
        client = new GreeterRpcClient("localhost", server.getPort());
    }

    @AfterAll
    static void stop() {
        server.stop();
    }

    @Test
    void sayHelloReturnsGreeting() throws Exception {
        assertEquals("Hello, Ada", client.sayHello("Ada"));
    }

    @Test
    void emptyNameReturnsErrorObject() {
        JsonRpcException ex = assertThrows(JsonRpcException.class, () -> client.sayHello(""));
        assertEquals(JsonRpcException.INVALID_PARAMS, ex.getCode());
        assertEquals("name must not be empty", ex.getMessage());
    }
}