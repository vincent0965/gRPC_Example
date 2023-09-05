package com.suns;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class GrpcClient6 {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext().build();
        try {
            HelloServiceGrpc.HelloServiceStub helloService = HelloServiceGrpc.newStub(managedChannel);
            //發送數據給server端
            StreamObserver<HelloProto.HelloRequest> helloRequestStreamObserver = helloService.cs2ss(new StreamObserver<HelloProto.HelloResponse>() {
                @Override
                public void onNext(HelloProto.HelloResponse helloResponse) {
                    System.out.println("回傳的結果: " + helloResponse.getResult());
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {
                    System.out.println("回傳全部結束");
                }
            });

            for (int i = 0 ; i < 10 ; i++){
                helloRequestStreamObserver.onNext(HelloProto.HelloRequest.newBuilder().setName("傳送給sever端的參數為:" + i).build());
            }
            helloRequestStreamObserver.onCompleted();

            //等待
            managedChannel.awaitTermination(15, TimeUnit.SECONDS);

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            managedChannel.shutdown();
        }
    }
}
