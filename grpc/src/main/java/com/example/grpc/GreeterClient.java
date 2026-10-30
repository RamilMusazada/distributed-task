package com.example.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;


public class GreeterClient implements AutoCloseable {

    public static final long DEADLINE_SECONDS = 2;

    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub stub;

    public GreeterClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.stub = GreeterGrpc.newBlockingStub(channel);
    }

    public String sayHello(String name) {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        return stub.withDeadlineAfter(DEADLINE_SECONDS, TimeUnit.SECONDS)
                .sayHello(request)
                .getMessage();
    }

    @Override
    public void close() throws InterruptedException {
        channel.shutdown();
        if (!channel.awaitTermination(2, TimeUnit.SECONDS)) {
            channel.shutdownNow();
        }
    }

    public static void main(String[] args) throws Exception {
        String name = args.length > 0 ? args[0] : "Ada";
        String host = args.length > 1 ? args[1] : "localhost";
        int port = args.length > 2 ? Integer.parseInt(args[2]) : GreeterServer.DEFAULT_PORT;

        try (GreeterClient client = new GreeterClient(host, port)) {
            System.out.println(client.sayHello(name));
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus().getCode()
                    + (e.getStatus().getDescription() != null ? " - " + e.getStatus().getDescription() : ""));
            System.exit(1);
        }
    }
}