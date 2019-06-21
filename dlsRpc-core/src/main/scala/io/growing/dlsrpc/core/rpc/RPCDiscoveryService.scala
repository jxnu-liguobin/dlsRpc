package io.growing.dlsrpc.core.rpc

import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.metadata.ServiceAddress
import io.growing.dlsrpc.consul.discovery.ServiceDiscovery
import javax.inject.Inject

/**
 * RPC服务发现接口，由其它包使用
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 */
class RPCDiscoveryService extends LazyLogging {

  @Inject
  private[this] final var serviceDiscovery: ServiceDiscovery = _

  /**
   * 根据服务名获取服务实际地址
   *
   * @param serviceName 服务名，被实例化Bean的名称
   * @return
   */
  def obtainServiceAddress(serviceName: String): ServiceAddress = {
    serviceDiscovery.discover(serviceName)
  }
}
