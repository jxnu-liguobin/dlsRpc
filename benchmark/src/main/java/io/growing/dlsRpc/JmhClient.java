package io.growing.dlsRpc;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.All)
public class JmhClient {
    @Benchmark
    @Group("JmhClient")
    @GroupThreads(35)
    public void getKey() {
        long t = System.currentTimeMillis();
        String s = ClientHello.hello.sayHello(t + "dls");
        if (!s.equals(t + "dls-hello!")) {
            new RuntimeException("error2");
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .forks(1)
                .include(JmhClient.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

}
