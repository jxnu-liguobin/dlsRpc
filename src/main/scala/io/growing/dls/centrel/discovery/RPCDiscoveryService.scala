package io.growing.dls.centrel.discovery

import com.typesafe.scalalogging.LazyLogging
import javax.inject.Inject

/**
 * RPC服务发现接口，由其它包使用
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 */
class RPCDiscoveryService extends LazyLogging {

  @Inject
  private[this] var serviceDiscovery: ServiceDiscovery = _

  def obtainService(serviceName: String): String = {
    serviceDiscovery.discover(serviceName)
  }
}