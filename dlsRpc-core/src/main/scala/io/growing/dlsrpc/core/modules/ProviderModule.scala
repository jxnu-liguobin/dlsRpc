package io.growing.dlsrpc.core.modules

import com.google.inject.AbstractModule
import io.growing.dlsrpc.common.metadata.ServiceAddress
import io.growing.dlsrpc.common.utils.Constants
import io.growing.dlsrpc.consul.discovery.{ConsulServiceDiscovery, ServiceDiscovery}
import io.growing.dlsrpc.consul.registry.{ConsulServiceRegistry, ServiceRegistry}
import io.growing.dlsrpc.core.api.{Protocol, Serializer}
import io.growing.dlsrpc.core.client.ClientChannel
import io.growing.dlsrpc.core.protocol.Http2Protocol
import io.growing.dlsrpc.core.rpc.{RPCDiscoveryService, RPCRegisterService}
import io.growing.dlsrpc.core.serialize.ProtostuffSerializer
import io.growing.dlsrpc.core.server.{Server, ServerChannel}
import io.growing.dlsrpc.core.transport.client.NettyClientChannel
import io.growing.dlsrpc.core.transport.server.NettyServerChannel

/**
 * guice接口和实现绑定注入
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-06
 */
class ProviderModule extends AbstractModule {

  override def configure(): Unit = {

    //传输协议
    bind(classOf[Protocol]).to(classOf[Http2Protocol]).asEagerSingleton()
    //序列化
    bind(classOf[Serializer]).to(classOf[ProtostuffSerializer]).asEagerSingleton()
    //客户端实现
    bind(classOf[ClientChannel]).to(classOf[NettyClientChannel]).asEagerSingleton()
    //服务端实现
    bind(classOf[ServerChannel]).to(classOf[NettyServerChannel]).asEagerSingleton()
    //服务注册
    bind(classOf[ServiceRegistry]).toInstance(new ConsulServiceRegistry(
      ServiceAddress(Constants.CONSUL_ADDRESS_IP, Constants.CONSUL_ADDRESS_PORT)))
    //服务发现
    bind(classOf[ServiceDiscovery]).toInstance(new ConsulServiceDiscovery(
      ServiceAddress(Constants.CONSUL_ADDRESS_IP, Constants.CONSUL_ADDRESS_PORT)))
    //RPC对外服务注册
    bind(classOf[RPCRegisterService]).asEagerSingleton()
    //RPC对外服务发现
    bind(classOf[RPCDiscoveryService]).asEagerSingleton()
    //使用依赖注入管理server
    bind(classOf[Server]).asEagerSingleton()

    //TODO 客户端需要泛型，不适合在这里处理，待优化
    //    bind(classOf[Client]).asEagerSingleton()
  }
}
