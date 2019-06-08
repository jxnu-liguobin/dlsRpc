package io.growing.dls.centrel.registry

import com.typesafe.scalalogging.LazyLogging
import io.growing.dls.meta.ServiceAddress
import io.growing.dls.utils
import javax.inject.Inject

import scala.collection.JavaConverters

/**
 * RPC服务注册，由其它包使用
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 */
class RPCRegisterService extends LazyLogging {


  //这里不能使用ServiceLoadUtil 可能是循环依赖，具体暂时不清楚
  @Inject
  private[this] var serviceRegistry: ServiceRegistry = _

  import io.growing.dls.Constants.PACKAGE_SERVICE

  /**
   * 获取所有需要注册的服务类名
   */
  def getServiceNames(): java.util.List[String] = {
    utils.ClassUtil.getClassListByAnnotation(PACKAGE_SERVICE, classOf[RPCService])
  }

  /**
   * 服务初始化时根据类名注册服务到consul
   *
   * @param serviceAddress 注册地址
   */
  def initRegisterService(serviceAddress: ServiceAddress): Unit = {
    for (serviceName <- JavaConverters.asScalaIterator(getServiceNames().iterator())) {
      serviceRegistry.register(serviceName, serviceAddress)
    }
    logger.info("InitRegisterService success")
  }

  /**
   * 启动后单个注册
   *
   * @param serviceName
   * @param serviceAddress
   */
  def registerService(serviceName: String, serviceAddress: ServiceAddress) = {
    logger.info(s"RegisterService : {$serviceName} success")
    serviceRegistry.register(serviceName, serviceAddress)
  }
}