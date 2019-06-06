package io.growing.dlsRpc;


import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import static io.growing.dls.client.ClientBuilder.builderClass;

public class ClientBuilderTest {


    public static void main(String[] args) {

        InetSocketAddress serviceAddress = InetSocketAddress.createUnresolved("127.0.0.1", 8012);
        Hello hello = builderClass(Hello.class).forAddress(serviceAddress).build();

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
