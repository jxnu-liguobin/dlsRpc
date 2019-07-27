package io.github.dlsrpc.core.consul

import com.typesafe.scalalogging.LazyLogging
import io.github.dlsrpc.common.config.Configuration
import io.github.dlsrpc.common.metadata.{NormalServiceAddress, ServiceAddress}
import io.github.dlsrpc.consul.discovery.ServiceDiscovery
import javax.inject.Inject

/**
 * RPC服务发现接口，由其它包使用
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 */
class RpcDiscoveryService extends LazyLogging {

  @Inject
  private[this] final var serviceDiscovery: ServiceDiscovery = _

  /**
   * 根据服务名获取服务实际地址
   *
   * @param serviceName 服务名，被实例化Bean的名称
   * @return
   */
  def obtainServiceAddress(serviceName: String): ServiceAddress = {
    if (Configuration.CONSUL_ENABLE) {
      serviceDiscovery.discover(serviceName)
    } else {
      //没有开启consul只能使用默认的本机，即未有集群
      new NormalServiceAddress(Configuration.DEFAULT_DISCOVER_ADDRESS)
    }
  }
}
