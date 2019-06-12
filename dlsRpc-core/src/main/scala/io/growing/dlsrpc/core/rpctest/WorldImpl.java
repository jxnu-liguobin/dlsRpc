package io.growing.dlsrpc.core.rpctest;

/**
 * 用于测试cglib，无接口
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-12
 */
public class WorldImpl {

    public String sayHello(String name) {
        return name.concat("-hello!");
    }
}
