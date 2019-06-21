package io.growing.dlsrpc.core.rpc

import java.util.{List => JList}

import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.config.DlsRpcConfiguration._
import io.growing.dlsrpc.common.metadata.ServiceAddress
import io.growing.dlsrpc.common.utils.ClassUtil
import io.growing.dlsrpc.common.utils.ImplicitUtils.jIteratorToSIterator
import io.growing.dlsrpc.consul.registry.{RPCService, ServiceRegistry}
import javax.inject.Inject

/**
 * RPC服务注册，由其它包使用
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-09
 */
class RPCRegisterService extends LazyLogging {

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
   * @param serviceAddress 注册地址
   */
  def initRegisterService(serviceAddress: ServiceAddress): Unit = {
    for (serviceName <- getServiceNames.iterator()) {
      serviceRegistry.register(serviceName, serviceAddress)
    }
  }

  /**
   * 启动后单个注册
   *
   * @param serviceName
   * @param serviceAddress
   */
  def registerService(serviceName: String, serviceAddress: ServiceAddress) = {
    serviceRegistry.register(serviceName, serviceAddress)
  }

}