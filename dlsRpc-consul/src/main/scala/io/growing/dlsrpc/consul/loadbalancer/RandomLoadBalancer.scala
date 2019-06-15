package io.growing.dlsrpc.consul.loadbalancer

import java.util.concurrent.ThreadLocalRandom
import java.util.{List => JList}

import io.growing.dlsrpc.common.utils.IsCondition

/**
 * 负载均衡
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 * @param serviceAddresses 所有存活的服务
 */
class RandomLoadBalancer[T](serviceAddresses: JList[T]) extends Loadbalancer[T] {

  /**
   * 服务的地址
   *
   * @return
   */
  def next: T = {
    IsCondition.conditionException(serviceAddresses.size() == 0, "can't find any serviceAddresses")
    serviceAddresses.get(ThreadLocalRandom.current.nextInt(serviceAddresses.size))
  }
}
