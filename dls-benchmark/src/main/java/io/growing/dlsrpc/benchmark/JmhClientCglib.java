package io.growing.dlsrpc.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 压测cglib代理
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-12
 */
public class JmhClientCglib {

    @Benchmark
    @GroupThreads(35)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @BenchmarkMode(Mode.Throughput)
    @Measurement(iterations = 10)
    @Warmup(iterations = 10)
    @Fork(1)
    public void getKey() {
        long t = System.currentTimeMillis();
        String s = ClientWorld.wrold.sayHello(t + "dls");
        if (!s.equals(t + "dls-hello!")) {
            new RuntimeException("error2");
        }
    }

    //先启动server，再压测
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .forks(1)
                .include(JmhClientJDK.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

}
