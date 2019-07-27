package io.growing.dlsrpc.test;

import io.github.dlsrpc.consul.registry.RPCService;

/**
 * 服务实现
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-19
 */
@RPCService
public class HelloWorld {

    public String sayHello(String name) {
        return name.concat("-hello!");
    }

}
