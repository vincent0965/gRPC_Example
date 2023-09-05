package com.suns;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class GrpcClient4 {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext().build();
        try{
            HelloServiceGrpc.HelloServiceStub helloService =  HelloServiceGrpc.newStub(managedChannel);
            HelloProto.HelloRequest.Builder builder = HelloProto.HelloRequest.newBuilder();
            builder.setName("我是非阻塞型的cilent");
            HelloProto.HelloRequest helloRequest = builder.build();
            //因為透過異步監聽的方式 所以c2s()不會只傳入一個參數
            //第一個參數是request 第二個參數是response(觀察server回傳結果)
            //可監聽的內容為onNext(資料內容)、onError(錯誤)、onComplete(結束)
            helloService.c2s(helloRequest, new StreamObserver<HelloProto.HelloResponse>() {
                @Override
                public void onNext(HelloProto.HelloResponse helloResponse) {
                    //server端回傳一個訊息後 就立刻處理
                    System.out.println("server每次回傳的訊息" + helloResponse.getResult());
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {
                    //取得全部server回傳的數據後再進行業務處理
                    System.out.println("server端回傳結束，後續再處理server資料");
                }
            });
            //因為非阻塞(newBlockingStub) 因此client會立刻關閉 需要等待一些時間 等server傳資料過來
            managedChannel.awaitTermination(15, TimeUnit.SECONDS);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            managedChannel.shutdown();
        }

    }
}
