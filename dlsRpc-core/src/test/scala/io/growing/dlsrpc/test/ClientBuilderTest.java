package io.growing.dlsrpc.test;


import io.growing.dlsrpc.core.DlsRpc;


/**
 * 手动测试rpc调用
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-12
 */
public class ClientBuilderTest {

    //先启动server
    public static void main(String[] args) {

//        HelloWorld hello = DlsRpcInvoke.getClientBuilder(HelloWorld.class).build();
//        Hello hello = DlsRpcInvoke.getClientBuilder(Hello.class).build();
        WorldImpl hello = DlsRpc.getClientBuilder(WorldImpl.class).build();
        for (int i = 0; i < 100000; i++) {
            System.out.println(hello.sayHello(i + "dls"));
        }
    }

}
