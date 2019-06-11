package io.growing.dlsrpc.benchmark;

import io.growing.dlsrpc.core.client.ClientBuilder;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.net.InetSocketAddress;


@State(value = Scope.Benchmark)
public class ClientHello {
    //cglib
    public static final HelloImpl hello = ClientBuilder.builderWithClass(HelloImpl.class).linkToAddress(
            InetSocketAddress.createUnresolved("127.0.0.1", 8080)).build();
}
