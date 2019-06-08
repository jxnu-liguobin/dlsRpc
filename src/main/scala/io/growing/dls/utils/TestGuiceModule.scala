package io.growing.dls.utils

import com.typesafe.scalalogging.LazyLogging
import io.growing.dls.centrel.discovery.ServiceDiscovery
import io.growing.dls.centrel.registry.RPCRegisterService

/**
 * 测试注入和获取服务名
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 */
object TestGuiceModule extends App with LazyLogging {

  private lazy val register: RPCRegisterService = ServiceLoadUtil.getProvider(classOf[RPCRegisterService])
  private lazy val discover: ServiceDiscovery = ServiceLoadUtil.getProvider(classOf[ServiceDiscovery])

  //单例对象有两个类名，一个是原始类，[TestGuiceModule$, TestGuiceModule]
  //  register.registerService("Hello", ServiceAddress("127.0.0.1", 8500))
  //巨坑，port无效，必须设置address的加port，而加了port后address又会报错
  println(discover.discover("Hello"))
}
