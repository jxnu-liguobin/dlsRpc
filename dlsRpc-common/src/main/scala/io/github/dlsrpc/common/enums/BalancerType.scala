package io.github.dlsrpc.common.enums

/**
 * 负载均衡类型
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-17
 */
object BalancerType extends Enumeration {

  type BalancerType = Value

  //随机
  val RANDOM = Value(0, "random-landbalancer")

  //加权随机，需要Hash，则多传一个参数
  val WEIGHT = Value(1, "weight-landbalancer")

}
