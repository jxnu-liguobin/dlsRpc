package io.growing.dlsrpc.benchmark.models;

import io.growing.dlsrpc.consul.registry.RPCService;

@RPCService
public interface Hello {

    String sayHello(String name);

}