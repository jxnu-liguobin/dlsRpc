package io.growing.dlsrpc.consul.commons

import java.util.concurrent.ConcurrentMap

import com.ecwid.consul.v1.{ConsulClient, ConsulRawClient}
import com.google.common.collect.Maps
import io.growing.dlsrpc.common.config.DlsRpcConfiguration._
import io.growing.dlsrpc.common.exception.RPCException
import io.growing.dlsrpc.common.metadata.{NormalServiceAddress, ServiceAddress}
import io.growing.dlsrpc.common.utils.IsCondition
import io.growing.dlsrpc.consul.loadbalancer.RandomLoadBalancer

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
  @deprecated
  def buildDiscover(consulAddress: ServiceAddress): (ConsulClient, ConcurrentMap[String, RandomLoadBalancer[ServiceAddress]]) = {
    IsCondition.conditionException(!consulAddress.toString.matches(PATTERN), "not an valid format like ip:port")
    IsCondition.conditionException(consulAddress.getPort < 0, "port can't less  0")
    val rawClient = new ConsulRawClient(consulAddress.getIp, consulAddress.getPort)
    val consulClient = new ConsulClient(rawClient)
    val loadBalancerMap = Maps.newConcurrentMap[String, RandomLoadBalancer[ServiceAddress]]
    (consulClient, loadBalancerMap)
  }

  /**
   * 根据consul地址构造注册客户端
   *
   * @param consulAddress
   * @return
   */
  @deprecated
  def buildRegistry(consulAddress: ServiceAddress): ConsulClient = {
    IsCondition.conditionException(!consulAddress.toString.matches(PATTERN), "not an valid format like ip:port")
    IsCondition.conditionException(consulAddress.getPort < 0, "port can't less  0")
    val rawClient = new ConsulRawClient(consulAddress.getIp, consulAddress.getPort)
    val consulClient = new ConsulClient(rawClient)
    consulClient
  }

  /**
   * 统一封装并返回
   *
   * @param consulAddress 只支持权值+Hash Ip
   * @return
   */
  def checkAndBuild(consulAddress: ServiceAddress): ConsulClient = {
    consulAddress match {
      case s: NormalServiceAddress => {
        IsCondition.conditionException(!s.toString.matches(PATTERN), "not an valid format like ip:port")
        IsCondition.conditionException(s.getPort < 0, "port can't less  0")
        lazy val rawClient = new ConsulRawClient(s.getIp, s.getPort)
        new ConsulClient(rawClient)
      }
      case _ => {
        //TODO
        throw RPCException("no matching type")
      }
    }
  }

}
