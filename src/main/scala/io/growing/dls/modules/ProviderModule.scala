package io.growing.dls.modules

import com.google.inject.AbstractModule
import io.growing.dls.client.ClientChannel
import io.growing.dls.protocol.Http2Protocol
import io.growing.dls.serialize.ProtostuffSerializer
import io.growing.dls.server.ServerChannel
import io.growing.dls.transport.client.NettyClientChannel
import io.growing.dls.transport.server.NettyServerChannel
import io.growing.dls.{Protocol, Serializer}

/**
 * guice接口和实现绑定注入
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-06
 */
class ProviderModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Protocol]).to(classOf[Http2Protocol]).asEagerSingleton()
    bind(classOf[Serializer]).to(classOf[ProtostuffSerializer]).asEagerSingleton()
    bind(classOf[ClientChannel]).to(classOf[NettyClientChannel]).asEagerSingleton()
    bind(classOf[ServerChannel]).to(classOf[NettyServerChannel]).asEagerSingleton()
  }
}
