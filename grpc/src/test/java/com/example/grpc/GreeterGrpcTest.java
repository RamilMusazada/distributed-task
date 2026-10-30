package com.example.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GreeterGrpcTest {

    private static GreeterServer server;
    private static GreeterClient client;

    @BeforeAll
    static void start() throws Exception {
        server = new GreeterServer(0).start(); // port 0 = any free port
        client = new GreeterClient("localhost", server.getPort());
    }

    @AfterAll
    static void stop() throws Exception {
        client.close();
        server.stop();
    }

    @Test
    void sayHelloReturnsGreeting() {
        assertEquals("Hello, Ada", client.sayHello("Ada"));
    }

    @Test
    void emptyNameReturnsInvalidArgument() {
        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class, () -> client.sayHello(""));
        assertEquals(Status.Code.INVALID_ARGUMENT, ex.getStatus().getCode());
    }
}