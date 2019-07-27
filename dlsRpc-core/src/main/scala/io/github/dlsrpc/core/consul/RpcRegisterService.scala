package io.github.dlsrpc.core.consul

import java.util.{List => JList}
import io.github.dlsrpc.common.utils.ImplicitUtils._
import io.github.dlsrpc.common.config.Configuration._
import com.typesafe.scalalogging.LazyLogging
import io.github.dlsrpc.common.config.Configuration
import io.github.dlsrpc.common.exception.RpcException
import io.github.dlsrpc.common.metadata.ServiceAddress
import io.github.dlsrpc.common.utils.{CheckCondition, ClassUtil}
import io.github.dlsrpc.consul.registry.{RPCService, ServiceRegistry}
import javax.inject.Inject

/**
 * RPC服务注册，由其它包使用
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-09
 */
class RpcRegisterService extends LazyLogging {

  //这里不能使用ServiceLoadUtil 可能是循环依赖，具体暂时不清楚
  @Inject
  private[this] final var serviceRegistry: ServiceRegistry = _

  /**
   * 获取所有需要注册的服务类名
   */
  def getServiceNames: JList[String] = {
    ClassUtil.getClassListByAnnotation(PACKAGE_SERVICE, classOf[RPCService])
  }

  /**
   * 服务初始化时根据类名注册服务到consul
   *
   * 未开启consul时，服务内部初始化时只需要打印出警告信息
   *
   * @param serviceAddress 注册地址
   */
  def initRegisterService(serviceAddress: ServiceAddress): Unit = {
    if (Configuration.CONSUL_ENABLE) {
      for (serviceName <- getServiceNames.iterator()) {
        serviceRegistry.register(serviceName, serviceAddress)
      }
    } else {
      CheckCondition.conditionWarn(!Configuration.CONSUL_ENABLE, "you have not enable the consul")
    }
  }

  /**
   * 启动后单个注册
   *
   * 尝试在未开启consul时调用注册接口应当抛出异常
   *
   * @param serviceName 服务注册的名称 如果注解用在接口，则使用接口名， 如果用在类中则使用类名
   * @param serviceAddress
   */
  def registerService(serviceName: String, serviceAddress: ServiceAddress) = {
    if (Configuration.CONSUL_ENABLE) {
      serviceRegistry.register(serviceName, serviceAddress)
    } else {
      throw RpcException("Unable to register because you have not enable the consul")
    }
  }

}