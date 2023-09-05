package com.suns;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient2 {
    public static void main(String[] args) {
        //創建通訊管道
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext().build();
        try {
            //獲得代理對象
            HelloServiceGrpc.HelloServiceBlockingStub helloService = HelloServiceGrpc.newBlockingStub(managedChannel);
            //完成rpc調用
            //準備參數(request)
            HelloProto.HelloRequest1.Builder builder = HelloProto.HelloRequest1.newBuilder();
            builder.addName("我是client端，我傳送一組資料過去");
            builder.addName("第一組數據");
            builder.addName("第二組數據");
            builder.addName("第三組數據");
            HelloProto.HelloRequest1 helloRequest1 = builder.build();
            //進行功能調用 獲取響應的內容
            HelloProto.HelloResponse1 helloResponse1 = helloService.hello1(helloRequest1);
            String result = helloResponse1.getResult();
            System.out.println(result);
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            //關閉連線通道
            managedChannel.shutdown();
        }
    }
}
