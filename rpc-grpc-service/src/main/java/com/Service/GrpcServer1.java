package com.Service;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer1 {
    public static void main(String[] args) throws InterruptedException, IOException {
        //port號的服務
        ServerBuilder serverBuilder = ServerBuilder.forPort(9000);
        //發佈服務
        serverBuilder.addService(new HelloServiceImpl());
        //建立服務對象
        Server server = serverBuilder.build();
        //啟用服務器
        server.start();
        server.awaitTermination();
    }
}
