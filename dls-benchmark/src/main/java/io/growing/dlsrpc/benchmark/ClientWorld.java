package io.growing.dlsrpc.benchmark;

import io.growing.dlsrpc.core.rpctest.WorldImpl;
import io.growing.dlsrpc.core.utils.DlsRpcInvoke;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * cglib代理
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-12
 */
@State(value = Scope.Benchmark)
public class ClientWorld {
    //jdk
    public static final WorldImpl wrold = DlsRpcInvoke.getClientBuilder(WorldImpl.class).build();

}
