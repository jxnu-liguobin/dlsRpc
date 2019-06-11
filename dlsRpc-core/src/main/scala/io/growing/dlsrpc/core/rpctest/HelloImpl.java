package io.growing.dlsrpc.core.rpctest;

import com.google.inject.Singleton;


/**
 * 用于测试cglib，没有接口
 */
@Singleton
public class HelloImpl implements Hello {

    public String sayHello(String name) {
        return name.concat("-hello!");
    }

}
