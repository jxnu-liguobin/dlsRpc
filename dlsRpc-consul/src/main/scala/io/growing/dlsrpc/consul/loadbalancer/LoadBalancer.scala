package io.growing.dlsrpc.consul.loadbalancer

import io.growing.dlsrpc.consul.loadbalancer.WeightLoadBalancer.WAMapType


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
   * 获取实时的某负载均衡实例含有的所有[server,weight]
   *
   * @return
   */
  def getServiceAddressMap: WAMapType = ???

  /**
   * 提供合并[server,weight]的权值
   *
   * @param addMaps
   * @return
   */
  def mergeWeight(addMaps: WAMapType): LoadBalancer[T] = ???

  /**
   * 给当前LoadBalancer增加Maps
   *
   * @param addMaps
   * @return
   */
  def mergeMaps(addMaps: WAMapType): LoadBalancer[T] = ???


}