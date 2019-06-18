package io.growing.dlsrpc.benchmark;

import io.growing.dlsrpc.core.rpctest.Hello;
import io.growing.dlsrpc.core.utils.DlsRpcInvoke;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * jdk代理
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-12
 */
@State(value = Scope.Benchmark)
public class ClientHello {

    public static final Hello hello = DlsRpcInvoke.getClientBuilder(Hello.class).build();

}
