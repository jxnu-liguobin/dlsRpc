package io.growing.dlsrpc.core.rpctest;

import com.google.inject.ImplementedBy;
import io.growing.dlsrpc.consul.registry.RPCService;

@RPCService
@ImplementedBy(HelloImpl.class) //使用guice 注册为bean
public interface Hello {
    String sayHello(String name);
}