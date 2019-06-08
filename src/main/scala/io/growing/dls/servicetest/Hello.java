package io.growing.dls.servicetest;

import com.google.inject.ImplementedBy;
import io.growing.dls.centrel.registry.RPCService;

@RPCService
@ImplementedBy(HelloImpl.class) //使用guice 注册为bean
public interface Hello {
    String sayHello(String name);
}
