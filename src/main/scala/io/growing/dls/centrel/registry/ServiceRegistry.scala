package io.growing.dls.centrel.registry

import io.growing.dls.meta.ServiceAddress

/**
 * 服务注册顶级接口
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 */
trait ServiceRegistry {

  /**
   * 服务注册
   *
   * @param serviceName    服务名
   * @param serviceAddress 注册地址 ip:port
   */
  def register(serviceName: String, serviceAddress: ServiceAddress): Unit

  /**
   * 剔除
   *
   * @param serviceName
   */
  def deregister(serviceName: String)

}
