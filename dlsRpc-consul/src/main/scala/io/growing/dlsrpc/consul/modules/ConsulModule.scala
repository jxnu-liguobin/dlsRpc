package io.growing.dlsrpc.consul.modules

import com.google.inject.AbstractModule
import io.growing.dlsrpc.consul.discovery.{ConsulServiceDiscovery, ServiceDiscovery}
import io.growing.dlsrpc.consul.registry.{ConsulServiceRegistry, ServiceRegistry}

/**
 * 模块化服务注册发现
 *
 * @author 梦境迷离
 * @version 1.0, 2019-07-13
 */
class ConsulModule extends AbstractModule {
  override def configure(): Unit = {
    //服务注册和发现
    bind(classOf[ServiceRegistry]).to(classOf[ConsulServiceRegistry]).asEagerSingleton()
    bind(classOf[ServiceDiscovery]).to(classOf[ConsulServiceDiscovery]).asEagerSingleton()
  }
}
