package io.growing.dlsrpc.test;

import com.google.inject.Singleton;


/**
 * 用于测试jdk代理，有接口
 */
@Singleton
public class HelloImpl implements Hello {

    public String sayHello(String name) {
        return name.concat("-hello!");
    }

}
