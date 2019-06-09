package io.growing.dls.modules

import com.google.inject.AbstractModule
import io.growing.dls.client.ClientChannel
import io.growing.dls.discovery.ServiceDiscovery
import io.growing.dls.discovery.consul.ConsulServiceDiscovery
import io.growing.dls.metadata.ServiceAddress
import io.growing.dls.protocol.Http2Protocol
import io.growing.dls.registry.ServiceRegistry
import io.growing.dls.registry.consul.ConsulServiceRegistry
import io.growing.dls.rpc.{RPCDiscoveryService, RPCRegisterService}
import io.growing.dls.serialize.ProtostuffSerializer
import io.growing.dls.server.ServerChannel
import io.growing.dls.transport.client.NettyClientChannel
import io.growing.dls.transport.server.NettyServerChannel
import io.growing.dls.utils.Constants
import io.growing.dls.{Protocol, Serializer}

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
  }
}
