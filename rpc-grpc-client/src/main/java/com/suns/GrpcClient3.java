package com.suns;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Iterator;

public class GrpcClient3 {
    public static void main(String[] args) {
        //創建通訊管道
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext().build();
        try {
            //獲得代理對象
            HelloServiceGrpc.HelloServiceBlockingStub helloService = HelloServiceGrpc.newBlockingStub(managedChannel);

            //完成rpc調用
            //準備參數(request)
            HelloProto.HelloRequest.Builder builder = HelloProto.HelloRequest.newBuilder();
            builder.setName("我是client端，我送資料過來，等待全部的資料回傳中");
            HelloProto.HelloRequest helloRequest = builder.build();

            //因為傳回來的參數是多個 因此使用迭代器(Iterator)接收
            Iterator<HelloProto.HelloResponse> helloResponseIterable = helloService.c2s(helloRequest);
            while(helloResponseIterable.hasNext()){
                HelloProto.HelloResponse helloResponse = helloResponseIterable.next();
                System.out.println("回傳的資料: "+helloResponse.getResult());
            }
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            //關閉連線通道
            managedChannel.shutdown();
        }
    }
}
