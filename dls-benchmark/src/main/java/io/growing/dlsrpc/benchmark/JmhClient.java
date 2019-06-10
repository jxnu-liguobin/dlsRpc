package io.growing.dlsrpc.benchmark;


import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class JmhClient {

    @Benchmark
    public void getKey() {
        long t = System.currentTimeMillis();
        String s = ClientHello.hello.sayHello(t + "dls");
        if (!s.equals(t + "dls-hello!")) {
            new RuntimeException("error2");
        }
    }

    //先启动server，再压测
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .forks(1).threadGroups(35).mode(Mode.All) //all模式测试所有
                .include(JmhClient.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

}
