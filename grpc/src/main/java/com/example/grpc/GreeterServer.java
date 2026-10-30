package com.example.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class GreeterServer {

    public static final int DEFAULT_PORT = 50051;

    private final Server server;

    public GreeterServer(int port) {
        this.server = ServerBuilder.forPort(port)
                .addService(new GreeterService())
                .build();
    }

    public GreeterServer start() throws IOException {
        server.start();
        return this;
    }


    public int getPort() {
        return server.getPort();
    }

    public void stop() throws InterruptedException {
        server.shutdown();
        if (!server.awaitTermination(5, TimeUnit.SECONDS)) {
            server.shutdownNow();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        server.awaitTermination();
    }

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        GreeterServer greeterServer = new GreeterServer(port).start();
        System.out.println("gRPC Greeter server listening on port " + greeterServer.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                greeterServer.stop();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
        greeterServer.blockUntilShutdown();
    }
}