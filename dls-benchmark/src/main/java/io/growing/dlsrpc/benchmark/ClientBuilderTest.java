package io.growing.dlsrpc.benchmark;


import io.growing.dlsrpc.core.rpctest.Hello;
import io.growing.dlsrpc.core.utils.DlsRpcInvoke;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * 手动测试rpc调用
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-12
 */
public class ClientBuilderTest {


    //先启动server
    public static void main(String[] args) {

        //Hello 测试jdk
        //WorldImpl 测试cglb
        Hello hello = DlsRpcInvoke.getClientBuilder(Hello.class).build();

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

        sim.execute(350, 60);
    }

}
