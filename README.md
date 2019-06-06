# A simple RPC implemented by Scala
  
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
* protostuff
* Jmh
* Guice

Test & Use
---

- git clone ```git@github.com:jxnu-liguobin/dlsRpc.git```
- sbt compile
- import use IDEA
- refresh maven module named benchmark 

Note
---

```
If you can't find a dependency in benchmark,then according to the prompt  to                                                                                     
 add dependency on module 'dlsRpc'
```

[@NettyRPC](http://www.cnblogs.com/jietang/p/5675171.html) [@grpc](https://github.com/grpc/grpc-java)