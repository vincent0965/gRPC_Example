package com.Service;

import com.google.protobuf.Message;
import com.google.protobuf.ProtocolStringList;
import com.suns.HelloProto;
import com.suns.HelloProto.HelloResponse;
import com.suns.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;

public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {
    /*
    1. 接受client提交的參數(by request)
    2. 業務處理 service dao 調用處理的業務功能
    3. 提供返回值
     */
    @Override
    public void hello(HelloProto.HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        //接受client提交的參數
        String name = request.getName();
        //業務處理
        System.out.println("接收到的訊息: "+ name);
        //提供返回值
        //1.創造響應對象的構造(HelloResponse)
        HelloResponse.Builder builder = HelloResponse.newBuilder();
        //2.填充數據
        builder.setResult("我是server端，接收到訊息，返回一個訊息");
        //3.封裝響應對象
        HelloResponse helloResponse = builder.build();

        //回傳client(StreamObserver)
        responseObserver.onNext(helloResponse);
        responseObserver.onCompleted();

    }

    @Override
    public void hello1(HelloProto.HelloRequest1 request, StreamObserver<HelloProto.HelloResponse1> responseObserver) {
        ProtocolStringList nameList = request.getNameList();
        for (String s : nameList){
            System.out.println("s: "+ s);
        }

        System.out.println("我是server端，我收到一組數據，回傳訊息");

        HelloProto.HelloResponse1.Builder builder = HelloProto.HelloResponse1.newBuilder();
        builder.setResult("okkkk");
        HelloProto.HelloResponse1 helloResponse1 = builder.build();

        responseObserver.onNext(helloResponse1); //透過gRPC將響應的訊息返回給client
        responseObserver.onCompleted(); //通知client端已經結束
    }

    //服務端RPC 發送多條訊息給client端 並且監控response
    @Override
    public void c2s(HelloProto.HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        //1. 接收clieant端的參數
        String name = request.getName();
        //2. 業務處理
        System.out.println("我收到client端的訊息(服務流RPC):" + name);
        //3. 根據業務處理結果回傳結果
        for(int i = 0 ; i < 9 ; i++){
            HelloProto.HelloResponse.Builder builder = HelloResponse.newBuilder();
            builder.setResult("接收處理的結果是: "+i);
            HelloProto.HelloResponse helloResponse = builder.build();

            //回傳給client端
            responseObserver.onNext(helloResponse);
            try{
                Thread.sleep(1000);
            } catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        }

        responseObserver.onCompleted(); //此時才告訴client端結束

    }

    //客戶端RPC的回傳值是StreamObserver 監控HelloRequest(因為不確定client何時發送數據過來)
    @Override
    public StreamObserver<HelloProto.HelloRequest> cs2s(StreamObserver<HelloResponse> responseObserver) {
        return new StreamObserver<HelloProto.HelloRequest>() {

            //監聽helloRequest的訊息
            @Override
            public void onNext(HelloProto.HelloRequest helloRequest) {
                System.out.println("接收到client發送的訊息: " + helloRequest.getName());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                System.out.println("client的訊息全部都發送到server端");

                //當接收client端的訊息，全部接收後處理完成並回傳給client
                HelloProto.HelloResponse.Builder builder = HelloProto.HelloResponse.newBuilder();
                builder.setResult("回傳server的結果");
                HelloProto.HelloResponse helloResponse = builder.build();

                responseObserver.onNext(helloResponse);
                responseObserver.onCompleted();
            }
        };
    }

    //雙向流RPC(雙邊多重發送訊息)
    @Override
    public StreamObserver<HelloProto.HelloRequest> cs2ss(StreamObserver<HelloResponse> responseObserver) {
        return new StreamObserver<HelloProto.HelloRequest>() {
            @Override
            public void onNext(HelloProto.HelloRequest helloRequest) {
                System.out.println("接收到client端傳送的訊息: " + helloRequest.getName());
                responseObserver.onNext(HelloProto.HelloResponse.newBuilder().setResult(
                        "response " + helloRequest.getName() + " result"
                ).build());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                System.out.println("接收到所有的client請求");
                responseObserver.onCompleted();
            }
        };
    }

}
