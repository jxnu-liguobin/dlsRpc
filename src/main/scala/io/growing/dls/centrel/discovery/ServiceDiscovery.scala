package io.growing.dls.centrel.discovery

/**
 * 服务发现顶级接口
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 */
trait ServiceDiscovery {

  /**
   * 服务发现
   *
   * @param serviceName class name
   * @return
   */
  def discover(serviceName: String): String

}
