package io.growing.dlsrpc.benchmark;


import io.growing.dlsrpc.core.utils.DlsRpcInvoke;

public class ServerBuilderTest {

    //sbt命令行执行 package 然后把core common consul (非dlsRpc.jar)的jar放到resources下面，替换原来的jar
    //先启动consul，再启动这个server
    public static void main(String[] args) {
        DlsRpcInvoke.publishService(8080, new HelloImpl());
    }
}
