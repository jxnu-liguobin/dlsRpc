package io.growing.dls.centrel.discovery.loadbalancer

import java.util.concurrent.ThreadLocalRandom

import io.growing.dls.utils.IsCondition

/**
 * 负载均衡
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 */
class RandomLoadBalancer[T](serviceAddresses: java.util.List[T]) extends Loadbalancer {

  /**
   * 服务的地址 ip:port
   *
   * @return
   */
  def next: T = {
    IsCondition.conditionException(serviceAddresses.size() == 0, "can't find any serviceAddresses")
    serviceAddresses.get(ThreadLocalRandom.current.nextInt(serviceAddresses.size))
  }
}
