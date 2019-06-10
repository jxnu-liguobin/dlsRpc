package io.growing.dlsrpc.benchmark;


import io.growing.dlsrpc.core.utils.DlsRpcInvoke;

import java.util.concurrent.atomic.AtomicInteger;

public class ClientBuilderTest {


    public static void main(String[] args) {

        Hello hello = DlsRpcInvoke.obtainService("127.0.0.1", 8080, Hello.class);

        for (int i = 0; i < 100000; i++) {
            assert (hello.sayHello(i + "dls")).equals(i + "dls-hello!");
        }

        AtomicInteger ao = new AtomicInteger(0);
        SimpleExecutor sim = new SimpleExecutor(() -> {
            int i = ao.incrementAndGet();
            String s = hello.sayHello(i + "dls");
            if (!s.equals(i + "dls-hello!")) {
                new RuntimeException("error");
            }
        });

        sim.execute(35, 60);
    }

}