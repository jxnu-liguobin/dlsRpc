package io.growing.dlsrpc.benchmark;

import io.growing.dlsrpc.core.client.ClientBuilder;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.net.InetSocketAddress;

/**
 * cglib代理
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-12
 */
@State(value = Scope.Benchmark)
public class ClientWorld {
    //jdk
    public static final WorldImpl world = ClientBuilder.builderWithClass(WorldImpl.class).linkToAddress(
            InetSocketAddress.createUnresolved("127.0.0.1", 8083)).build();
}
