package io.growing.dlsrpc.core.rpctest;

import io.growing.dlsrpc.consul.registry.RPCService;

/**
 * 用于测试cglib，无接口
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-12
 */
@RPCService
public class WorldImpl {

    public String sayHello(String name) {
        return name.concat("-hello!");
    }
}
