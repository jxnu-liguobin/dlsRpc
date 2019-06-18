package io.growing.dlsrpc.core.rpctest;

import io.growing.dlsrpc.consul.registry.RPCService;

@RPCService
public interface Hello {

    String sayHello(String name);

}
