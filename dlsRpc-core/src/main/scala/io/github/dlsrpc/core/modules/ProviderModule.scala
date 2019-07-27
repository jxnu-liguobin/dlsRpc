package io.github.dlsrpc.core.modules

import com.google.inject.AbstractModule
import io.github.dlsrpc.core.api.{Protocol, Serializer}
import io.github.dlsrpc.core.client.ClientChannel
import io.github.dlsrpc.core.consul.{RpcDiscoveryService, RpcRegisterService}
import io.github.dlsrpc.core.protocol.Http2Protocol
import io.github.dlsrpc.core.serialize.ProtostuffSerializer
import io.github.dlsrpc.core.server.ServerChannel
import io.github.dlsrpc.core.transport.client.NettyClientChannel
import io.github.dlsrpc.core.transport.server.NettyServerChannel

/**
 * guice接口和实现绑定注入
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-06
 */
class ProviderModule extends AbstractModule {

  //这里进行绑定的bean是系统顶级接口的实现类，不再这里绑定类使用了单例注解
  override def configure(): Unit = {

    //传输协议
    bind(classOf[Protocol]).to(classOf[Http2Protocol]).asEagerSingleton()
    //序列化
    bind(classOf[Serializer]).to(classOf[ProtostuffSerializer]).asEagerSingleton()
    //客户端实现
    bind(classOf[ClientChannel]).to(classOf[NettyClientChannel]).asEagerSingleton()
    //服务端实现
    bind(classOf[ServerChannel]).to(classOf[NettyServerChannel]).asEagerSingleton()
    //RPC对外服务注册
    bind(classOf[RpcRegisterService]).asEagerSingleton()
    //RPC对外服务发现
    bind(classOf[RpcDiscoveryService]).asEagerSingleton()
  }
}
