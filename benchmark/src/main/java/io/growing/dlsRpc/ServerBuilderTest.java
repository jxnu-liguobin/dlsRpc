package io.growing.dlsRpc;

import io.growing.dls.DlsRpcInvoke;

public class ServerBuilderTest {

    public static void main(String[] args) {
        Hello hello = new HelloImpl();
        DlsRpcInvoke.publishService(8889, hello);
    }
}
