package io.growing.dlsrpc.test;

import io.github.dlsrpc.consul.registry.RPCService;

@RPCService
public interface Hello {

    String sayHello(String name);

}
