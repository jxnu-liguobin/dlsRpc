package io.github.dlsrpc.consul.registry

import java.util.{ArrayList => JArrayList}
import io.github.dlsrpc.common.config.Configuration._
import com.ecwid.consul.v1.agent.model.NewService
import com.typesafe.scalalogging.LazyLogging
import io.github.dlsrpc.common.metadata.ServiceAddress
import io.github.dlsrpc.common.utils.CheckCondition
import io.github.dlsrpc.consul.commons.ConsulBuilder

/**
 * 使用consul的服务注册
 *
 * @author 梦境迷离
 * @version 1.2, 2019-06-08
 */
class ConsulServiceRegistry extends ServiceRegistry with LazyLogging {

  private[this] final lazy val consulClient = ConsulBuilder.checkAndBuild

  override def register(serviceName: String, serviceAddress: ServiceAddress): Unit = {
    CheckCondition.conditionException(serviceAddress.getPort < 0, "port can't less  0")
    val newService = new NewService
    newService.setId(generateNewIdForService(serviceName, serviceAddress))
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

  /**
   * 自定义服务ID，解析服务被覆盖
   *
   * @param serviceName    服务名
   * @param serviceAddress 服务地址
   * @return serviceNames-ip:port
   */
  private[this] def generateNewIdForService(serviceName: String, serviceAddress: ServiceAddress): String = {
    serviceName + "-" + serviceAddress.toString
  }


  //手动根据id清除，id需要转码
  //PUT http://127.0.0.1:8500/v1/agent/service/deregister/Hello-127.0.0.1
  def deregister(serviceId: String) = {
    consulClient.agentServiceDeregister(serviceId)
  }
}