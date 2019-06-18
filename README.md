# A simple RPC implemented by Scala

High performance RPC framework based on HTTP/2 Protocol, Consul, Guice, Netty Transport and Protostuff Serialization.

If you want to join this project, please contact me by email.（想加入本项目的请邮件联系我）

  
* Java 8
* Scala 2.12.7

[![Build Status](https://travis-ci.org/jxnu-liguobin/dlsRpc.svg?branch=master)](https://travis-ci.org/jxnu-liguobin/dlsRpc)
![GitHub](https://img.shields.io/github/license/jxnu-liguobin/dlsRpc.svg)
![GitHub top language](https://img.shields.io/github/languages/top/jxnu-liguobin/dlsRpc.svg)
---


Already Implemented functions
---

- Remote Call base on NIO （基于 NIO 的远程调用）
- Service Discovery （基于 consul-api 的服务发现）
- Service Registration base on annotation （基于自定义注解的服务注册）
- Server-side LoadBalancing with Random, Weight or Hash IP （基于随机、权重、权重 & HASH IP 的服务端负载均衡）
- Automatic switching between CGLIB and JDK proxy （自动切换 CGLIB、JDK 动态代理）
- Dependency Injection base on Guice
- Serializer base on Protostuff
- Call chaining
- Multi-module project base on SBT
- Pressure measurement base on JMH
- Configuration injection based on typesafe Config


- loading  


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
- run sbt task: publishM2

Examples
---

- server 

```java
List<Object> tmpList1 = new ArrayList<>();
tmpList1.add(new WorldImpl()); //CGLIB PRXOY
List<Object> tmpList2 = new ArrayList<>();
tmpList2.add(new HelloImpl()); //JDK PROXY
Seq<Object> tmpSeq1 = JavaConverters.asScalaIteratorConverter(tmpList1.iterator()).asScala().toSeq();//init need
Seq<Object> tmpSeq2 = JavaConverters.asScalaIteratorConverter(tmpList2.iterator()).asScala().toSeq();//add publish bean
//Demonstration of instantiating publishing services through chain invocation
ServerBuilder server = DlsRpcInvoke.getServerBuilder(8080, tmpSeq1).publishServices(tmpSeq2);
server.build().start();
```
- client

```java
Hello hello = DlsRpcInvoke.getClientBuilder(Hello.class).build();
```

Note
---

```
set maven home,like /.m2/repository where benchmark module can be find
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

