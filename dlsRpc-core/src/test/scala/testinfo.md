前排警告
---

* 安装 Consul 单机
* 安装Scala 2.12.x
* 安装 Java 8
* 需要压测就安装 IDEA 的 JMH插件
* 不要使用我核心包的models，那是测试用的！
* 基于JDK代理的RPC，接口和实现需要同包或者实现类在子包！
* dlsRPC.conf 可配置consul是否开启，默认开！
* 服务端启动后马上停止且状态码 0 ，一般是端口通道打开失败！
* 如果不会使用 sbt publish 那就使用 resources 下的jar
* 必须将 dlsrpc.consul.registry.package-service 属性设置为自己项目的顶级包名！


下面是最小配置项

```
//使用此RPC的项目需要提供dlsRpc.conf文件，否则使用默认配置。
//调用Configuration.config(filePath)，可以使用其他config文件夹下的配置文件
//这配置文件给本测试用的，也是本地测试时最少配置

//默认注册的包下的service，需要改为自己项目的顶级包
dlsrpc.consul.registry.package-service = "io.growing.dlsrpc.test"

//若忘记配dlsrpc.consul.server.address或未开启consul，则认为是本地调用，将使用本地服务地址
//需要与服务端启动时的端口相同
dlsrpc.server.address.default = "127.0.0.1:8080"
```

下面 是 benchmark 模块的一个可用的 dlsRPC.conf 完整配置文件，直接放到自己项目的 resources 下面即可

```
//这是默认的
//使用此RPC的项目需要提供dlsRpc.conf文件，否则使用默认配置。
//调用Configuration.config(filePath)，可以使用其他config文件夹下的配置文件

//consul ip，需要抽出去为用户配置
dlsrpc.consul.host = "127.0.0.1"

//consul 端口，需要抽出去为用户配置
dlsrpc.consul.port = 8500

//consul 服务健康检查间隔时间 1秒
dlsrpc.consul.interval = 1s

//默认开启服务注册
dlsrpc.consul.enable = true

//服务器端所有可用ip，用于负载均衡，需要抽出去为用户配置
//此配置不能完全决定服务地址的选取，但是在此配置的ip将被负载均衡优先选择
dlsrpc.consul.server.address = ["127.0.0.1"]

//ip:port验证
dlsrpc.consul.adress-pattern = "(?:(?:[0,1]?\\d?\\d|2[0-4]\\d|25[0-5])\\.){3}(?:[0,1]?\\d?\\d|2[0-4]\\d|25[0-5]):\\d{0,5}"

//仅ip验证
dlsrpc.consul.ip-pattern = "(?:(?:[0,1]?\\d?\\d|2[0-4]\\d|25[0-5])\\.){3}(?:[0,1]?\\d?\\d|2[0-4]\\d|25[0-5])"

//默认注册的包下的service，需要抽出去为用户配置
dlsrpc.consul.registry.package-service = "io.growing.dlsrpc"

//客户端超时时间，需要抽出去为用户配置
dlsrpc.client.timeout = 30000

//请求id，一般从 0 开始自增
dlsrpc.client.request-start-value = 0

//HTTP 消息长度，TCP解码用
dlsrpc.http.message-length = 4

//默认是服务调用地址就是本地，需要抽出去为用户配置或改为缓存地址到本地
dlsrpc.server.address.default = "localhost:8080"

//web服务默认端口，需要抽出去为用户配置
//需要与服务启动时的端口相同
dlsrpc.server.port = 8080

//默认WEB服务在本地，需要抽出去为用户配置
dlsrpc.server.ip = "127.0.0.1"

//负载均衡默认权值
dlsrpc.server.balancer-weight = 5

//默认启用cglib
dlsrpc.proxy.mode.cglib-proxy = true

//是否强制
dlsrpc.proxy.mode.force-cglib-proxy = false
```

Benchmark
---

until v1.0.13

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

[@NettyRPC](http://www.cnblogs.com/jietang/p/5675171.html) [@grpc](https://github.com/grpc/grpc-java) [@Reference Java Edition](https://github.com/yeyincai/flashRPC)

[@NioEventLoopGroup](https://www.jianshu.com/p/2e3ae43dc4cb) [@consul-api](https://github.com/Ecwid/consul-api) [@EnableEurekaClient Source Analysis](https://www.cnblogs.com/zhangjianbin/p/6616866.html)

