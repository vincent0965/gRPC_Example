package com.suns;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class GrpcClient5 {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext().build();
        try{
            HelloServiceGrpc.HelloServiceStub helloService = HelloServiceGrpc.newStub(managedChannel);
            //因為client被監聽 所以要使用responseObserver來監聽server回傳的結果
            StreamObserver<HelloProto.HelloRequest> helloRequestStreamObserver = helloService.cs2s(new StreamObserver<HelloProto.HelloResponse>() {
                @Override
                public void onNext(HelloProto.HelloResponse helloResponse) {
                    //監控server回應
                    System.out.println("server回傳的訊息為: " + helloResponse.getResult());
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {
                    //監控到server回應結束的標籤
                    System.out.println("server回應結束");
                }
            });

            //client向server發送數據(不定時多條數據)
            for (int i = 0; i< 10; i++){
                HelloProto.HelloRequest.Builder builder = HelloProto.HelloRequest.newBuilder();
                builder.setName(" 我是非阻塞型而且持續傳參數的client :"+ i);
                HelloProto.HelloRequest helloRequest = builder.build();

                helloRequestStreamObserver.onNext(helloRequest);
                Thread.sleep(1000);
            }
            helloRequestStreamObserver.onCompleted();
            //等待server回應
            managedChannel.awaitTermination(15, TimeUnit.SECONDS);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            managedChannel.shutdown();
        }

    }
}
