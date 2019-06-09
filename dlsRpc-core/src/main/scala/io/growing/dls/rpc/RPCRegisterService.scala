package io.growing.dls.rpc

import java.util.{List => JList}

import com.typesafe.scalalogging.LazyLogging
import io.growing.dls.metadata.ServiceAddress
import io.growing.dls.registry.{RPCService, ServiceRegistry}
import io.growing.dls.utils.{ClassUtil, Constants}
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
  private[this] var serviceRegistry: ServiceRegistry = _


  /**
   * 获取所有需要注册的服务类名
   */
  def getServiceNames: JList[String] = {
    ClassUtil.getClassListByAnnotation(Constants.PACKAGE_SERVICE, classOf[RPCService])
  }

  /**
   * 服务初始化时根据类名注册服务到consul
   *
   * @param serviceAddress 注册地址
   */
  def initRegisterService(serviceAddress: ServiceAddress): Unit = {
    //TODO 注册前检查实现类是否存在，避免注册无效服务
    import io.growing.dls.utils.ImplicitUtils.javaItToScalaIt // 隐式对象
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