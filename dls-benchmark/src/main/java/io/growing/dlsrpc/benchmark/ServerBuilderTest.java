package io.growing.dlsrpc.benchmark;


import io.growing.dlsrpc.core.utils.DlsRpcInvoke;

public class ServerBuilderTest {

    public static void main(String[] args) {
        DlsRpcInvoke.publishService(8080, new HelloImpl());
    }
}
