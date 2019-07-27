package io.github.dlsrpc.consul.loadbalancer

import java.util.concurrent.ThreadLocalRandom
import java.util.{List => JList}

import io.github.dlsrpc.common.utils.CheckCondition

/**
 * 负载均衡
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 * @param serviceAddresses 所有存活的服务
 */
class RandomLoadBalancer[+T](serviceAddresses: JList[T]) extends LoadBalancer[T] {

  /**
   * 服务的地址
   *
   * @return
   */
  def next: T = {
    CheckCondition.conditionException(serviceAddresses.size() == 0, "can't find any serviceAddresses")
    serviceAddresses.get(ThreadLocalRandom.current.nextInt(serviceAddresses.size))
  }
}
