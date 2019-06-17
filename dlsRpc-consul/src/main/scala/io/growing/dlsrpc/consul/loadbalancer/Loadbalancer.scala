package io.growing.dlsrpc.consul.loadbalancer

/**
 * 服务发现负载均衡顶级接口
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 */
trait Loadbalancer[T] {


  /**
   * 权值 + 默认权值
   *
   * @return
   */
  def next: T


  /**
   * HASH IP + 权值 + 默认权值
   *
   * @param remoteIp
   * @return
   */
  def next(remoteIp: String): T = ???

}
