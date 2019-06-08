package io.growing.dlsRpc;

import io.growing.dls.DlsRpcInvoke;

public class ServerBuilderTest {

    public static void main(String[] args) {
        DlsRpcInvoke.publishService(8877, new HelloImpl());
    }
}
