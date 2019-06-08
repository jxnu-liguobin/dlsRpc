package io.growing.dls.centrel.discovery

import java.util.concurrent.ConcurrentHashMap

import com.ecwid.consul.v1.{ConsulClient, ConsulRawClient}
import io.growing.dls.centrel.discovery.loadbalancer.RandomLoadBalancer
import io.growing.dls.meta.ServiceAddress
import io.growing.dls.utils.{Constants, IsCondition}

/**
 * 封装构造逻辑
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-09
 */
object ConsulBuilder {

  /**
   * 根据consul地址构造发现客户端
   *
   * @param consulAddress ip port
   * @return
   */
  def buildDiscover(consulAddress: ServiceAddress): (ConsulClient, ConcurrentHashMap[String, RandomLoadBalancer[ServiceAddress]]) = {
    IsCondition.conditionException(!consulAddress.toString.matches(Constants.PATTERN), "ip invalid")
    IsCondition.conditionException(consulAddress.getPort < 0, "port can't less  0")
    val rawClient = new ConsulRawClient(consulAddress.getIp, consulAddress.getPort)
    val consulClient = new ConsulClient(rawClient)
    val loadBalancerMap = new ConcurrentHashMap[String, RandomLoadBalancer[ServiceAddress]]
    (consulClient, loadBalancerMap)
  }

  /**
   * 根据consul地址构造注册客户端
   *
   * @param consulAddress
   * @return
   */
  def buildRegistry(consulAddress: ServiceAddress): ConsulClient = {
    IsCondition.conditionException(!consulAddress.toString.matches(Constants.PATTERN), "ip invalid")
    IsCondition.conditionException(consulAddress.getPort < 0, "port can't less  0")
    val rawClient = new ConsulRawClient(consulAddress.getIp, consulAddress.getPort)
    val consulClient = new ConsulClient(rawClient)
    consulClient
  }

}
