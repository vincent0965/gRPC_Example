package com.suns;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient1 {
    public static void main(String[] args) {
        //創建通訊管道
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext().build();
        try {
            //獲得代理對象
            HelloServiceGrpc.HelloServiceBlockingStub helloService = HelloServiceGrpc.newBlockingStub(managedChannel);
            //完成rpc調用
            //準備參數(request)
            HelloProto.HelloRequest.Builder builder = HelloProto.HelloRequest.newBuilder();
            builder.setName("我是client端，我送資料過來");
            HelloProto.HelloRequest helloRequest = builder.build();
            //進行功能調用 獲取響應的內容
            //監控onCompleted() => 接收到訊息表示server已經收到資料並要結束這次的溝通
            HelloProto.HelloResponse helloResponse = helloService.hello(helloRequest);
            String result = helloResponse.getResult();
            System.out.println(result);
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            //關閉連線通道
            managedChannel.shutdown();
        }
    }
}
