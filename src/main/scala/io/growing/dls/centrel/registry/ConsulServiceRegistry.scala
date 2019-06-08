package io.growing.dls.centrel.registry

import java.util

import com.ecwid.consul.v1.agent.model.NewService
import com.ecwid.consul.v1.{ConsulClient, ConsulRawClient}
import com.typesafe.scalalogging.LazyLogging
import io.growing.dls.Constants
import io.growing.dls.meta.ServiceAddress
import io.growing.dls.utils.IsCondition

/**
 * 使用consul的服务注册
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 * @param consulAddress ip:port
 */
class ConsulServiceRegistry(consulAddress: String) extends ServiceRegistry with LazyLogging {

  IsCondition.conditionException(!consulAddress.matches(Constants.PATTERN), "ip invalid")
  lazy val address = consulAddress.split(":")
  IsCondition.conditionException(address(1).toInt < 0, "port can't less  0")
  lazy val rawClient = new ConsulRawClient(address(0), Integer.valueOf(address(1)))
  lazy val consulClient = new ConsulClient(rawClient)

  override def register(serviceName: String, serviceAddress: ServiceAddress): Unit = {
    val newService = new NewService
    val id = generateNewIdForService(serviceName)
    newService.setId(id)
    newService.setName(serviceName)
    newService.setTags(new util.ArrayList)
    newService.setAddress(serviceAddress.getIp)
    newService.setPort(serviceAddress.getPort)
    // Set health check
    val check = new NewService.Check()
    check.setTcp(serviceAddress.toString)
    check.setInterval("1s")
    newService.setCheck(check)
    consulClient.agentServiceRegister(newService)
  }

  private[this] def generateNewIdForService(serviceName: String): String = {
    serviceName + "-" + Constants.CONSUL_ADDRESS
  }


  def deregister(serviceId: String) = {
    consulClient.agentServiceDeregister(serviceId)
    //手动根据id清除，id需要转码
    //PUT http://127.0.0.1:8500/v1/agent/service/deregister/Hello-127.0.0.1
  }
}