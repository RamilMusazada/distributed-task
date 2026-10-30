package com.example.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;


public class GreeterService extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        String name = request.getName();
        if (name.isBlank()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("name must not be empty")
                    .asRuntimeException());
            return;
        }
        responseObserver.onNext(HelloReply.newBuilder().setMessage("Hello, " + name).build());
        responseObserver.onCompleted();
    }
}