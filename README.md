# A simple RPC implemented by Scala

High performance RPC framework based on HTTP/2 Protocol, Consul, Guice, Netty Transport and Protostuff Serialization
  
* Java 8
* Scala 2.12.7

[![Build Status](https://travis-ci.org/jxnu-liguobin/dlsRpc.svg?branch=master)](https://travis-ci.org/jxnu-liguobin/dlsRpc)
![GitHub](https://img.shields.io/github/license/jxnu-liguobin/dlsRpc.svg)
![GitHub top language](https://img.shields.io/github/languages/top/jxnu-liguobin/dlsRpc.svg)
---

Technology
---

* Netty 
* Http2 
* Protostuff
* Jmh
* Guice
* Consul
* Cglib

Test & Use
---

- git clone ```git@github.com:jxnu-liguobin/dlsRpc.git```
- sbt compile
- import use IDEA
- refresh maven module named benchmark 

Examples
---

- server publish

```
in java
Hello hello = new HelloImpl();
DlsRpcInvoke.publishService(8889, hello);

in scala
val hello = new HelloImpl()
DlsRpcInvoke.publishService(8889, hello)
```
- client obtain

```
in java
Hello hello = DlsRpcInvoke.obtainService("127.0.0.1", 8889, Hello.class);

in scala
val hello = DlsRpcInvoke.obtainService("127.0.0.1", 8889, classOf[Hello])
```

Note
---

```
The benchmark module use dlsRpc by dlsRpc-*.jar where is in their target folders after you package
```

Benchmark
---

- Condition 

| Title | Value |
| --- | --- |
| CPU | i5 7300HQ |
| Memory | 12G DDR4(2400MHz) |
| Threads | 35 |

- Details

| Benchmark | Mode | Cnt | Score | Error | Units |
| --- | --- | --- | --- | --- | --- |
| dlsRpc.JmhClient.JmhClient | Throughput |  20 | 25.144 ± 1.576 |  | ops/ms |
| dlsRpc.JmhClient.JmhClient | Average time |  20 | 1.339 ± 0.029 |  | ms/op |
| dlsRpc.JmhClient.JmhClient | Sampling time |  571301 | 1.309 ± 0.005 | | ms/op |
| dlsRpc.JmhClient.JmhClient | Single shot invocation time |   |  2567.914 |   | ms/op |

[@NettyRPC](http://www.cnblogs.com/jietang/p/5675171.html) [@grpc](https://github.com/grpc/grpc-java) [@Original Java Edition](https://github.com/yeyincai/flashRPC)

[@NioEventLoopGroup](https://www.jianshu.com/p/2e3ae43dc4cb) [@consul-api](https://github.com/Ecwid/consul-api) [@EnableEurekaClient源码分析](https://www.cnblogs.com/zhangjianbin/p/6616866.html)

