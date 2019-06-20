package io.growing.dlsrpc.consul.registry

import java.util.{ArrayList => JArrayList}

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
    newService.setId(generateNewIdForService(serviceName))
    newService.setName(serviceName)
    newService.setTags(new JArrayList)
    newService.setAddress(serviceAddress.getIp)
    newService.setPort(serviceAddress.getPort)
    // Set health check
    val check = new NewService.Check()
    //TODO 默认健康检查的请求地址是服务ip:默认WEB端口，这里只是为了测试，使用consul的端口
    check.setTcp(serviceAddress.getIp + ":" + 8500) //TCP检查端口改成8500 好通过/health
    check.setInterval(CONSUL_INTERVAL)
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