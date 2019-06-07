package io.growing.dlsRpc;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.net.InetSocketAddress;

import static io.growing.dls.client.ClientBuilder.builderClass;

@State(value = Scope.Benchmark)
public class ClientHello {
   public static final Hello hello = builderClass(Hello.class).forAddress(
            InetSocketAddress.createUnresolved("127.0.0.1", 8012)).build();
}
