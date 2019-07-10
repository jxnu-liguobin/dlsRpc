package io.growing.dlsrpc.consul.loadbalancer

import java.util.{Map => JMap}

import io.growing.dlsrpc.common.metadata.ServiceAddress


/**
 * 服务发现负载均衡顶级接口
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-08
 */
trait LoadBalancer[+T] {

  /**
   * 权值 + 默认权值
   *
   * @return
   */
  def next: T

  /**
   * HASH Client IP + 权值 + 默认权值
   *
   * @param remoteIp
   * @return
   */
  def next(remoteIp: String): T = ???

  /**
   * 提供在轮询时更新[server,weight]的接口
   *
   * @param addMaps
   * @return
   */
  def ++(addMaps: JMap[_ <: ServiceAddress, Int]): LoadBalancer[T] = ???

  /**
   * 获取实时的某负载均衡实例含有的所有[server,weight]
   *
   * @return
   */
  def getServiceAddressMap: JMap[_ <: ServiceAddress, Int] = ???

}