package io.growing.dlsRpc;

import io.growing.dls.server.Server;
import io.growing.dls.server.ServerBuilder;

import java.util.concurrent.TimeUnit;

public class ServerBuilderTest {

    public static void main(String[] args) throws InterruptedException {
        Hello hello = new HelloImpl();
        Server server = ServerBuilder.forPort(8889).publishService(hello).build();
        server.start();
        TimeUnit.SECONDS.sleep(1000);
        server.shutdown();
    }
}
