# A simple RPC implemented by Scala

High performance RPC framework based on HTTP/2 Protocol, Consul, Guice, Netty Transport and Protostuff Serialization.

If you want to join this project, please contact me by email.

    You'd better know what technologys Java Scala SBT RPC are

* Java 8
* Scala 2.12.7

[![Build Status](https://travis-ci.org/jxnu-liguobin/dlsRpc.svg?branch=master)](https://travis-ci.org/jxnu-liguobin/dlsRpc)
![GitHub](https://img.shields.io/github/license/jxnu-liguobin/dlsRpc.svg)
![GitHub top language](https://img.shields.io/github/languages/top/jxnu-liguobin/dlsRpc.svg)

Already Implemented functions
---

- Remote Call base on NIO
- Service Discovery
- Service Registration base on annotation
- Server-side LoadBalancing with Random, Weight or Hash IP
- Automatic switching between CGLIB and JDK proxy
- Dependency Injection base on Guice
- Serializer base on Protostuff
- Call chaining
- Multi-module project base on SBT
- Pressure measurement base on JMH
- Configuration injection base on typesafe Config
- Services can be configured and configuration file can be configured
- Client Configuration Caching and Timely Refresh
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
* Typesafe Config
* Guava Cache

How to use
---

1.libraryDependencies in build.sbt
```sbt
    io.github.jxnu-liguobin % dlsrpc-common_2.12 % 1.1.1
    io.github.jxnu-liguobin % dlsrpc-consul_2.12 % 1.1.1
    io.github.jxnu-liguobin % dlsrpc-core_2.12 % 1.1.1
```
2.start consul in default port 

3.use in java

- server 

```java
//you need start a consul
//start server
public class ServerBuilderTest {
    public static void main(String[] args) {
        List<Object> tmpList1 = new ArrayList<>();
        tmpList1.add(new HelloWorld());
        ServerBuilder server = DlsRpc.getServerBuilder(9000, tmpList1);
        server.build().start();
    }
}
//service product
@RPCService
public class HelloWorld {

    public String sayHello(String name) {
        return name.concat("-hello!");
    }

}
```
- client

```java
//start client
//no super interface
HelloWorld hello = DlsRpc.getClientBuilder(HelloWorld.class).build();
```
