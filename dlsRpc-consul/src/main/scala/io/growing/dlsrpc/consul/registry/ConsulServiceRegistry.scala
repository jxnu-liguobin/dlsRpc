package io.growing.dlsrpc.consul.registry

import java.util

import com.ecwid.consul.v1.agent.model.NewService
import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.config.DlsRpcConfiguration._
import io.growing.dlsrpc.common.metadata.ServiceAddress
import io.growing.dlsrpc.consul.commons.ConsulBuilder

/**
 * 使用consul的服务注册
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 * @param consulAddress ip:port
 */
class ConsulServiceRegistry(consulAddress: ServiceAddress) extends ServiceRegistry with LazyLogging {

  private[this] final lazy val consulClient = ConsulBuilder.checkAndBuild(consulAddress)

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
    logger.info(s"RegisterService : {$serviceName} success")

  }

  //暂时使用这种做服务id名
  private[this] def generateNewIdForService(serviceName: String): String = {
    serviceName + "-" + CONSUL_ADDRESS_IP + ":" + CONSUL_ADDRESS_PORT
  }

  //手动根据id清除，id需要转码
  //PUT http://127.0.0.1:8500/v1/agent/service/deregister/Hello-127.0.0.1
  def deregister(serviceId: String) = {
    consulClient.agentServiceDeregister(serviceId)
  }
}