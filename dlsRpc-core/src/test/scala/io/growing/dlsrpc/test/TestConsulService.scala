package io.growing.dlsrpc.test

import io.growing.dlsrpc.common.metadata.WeightServiceAddress
import io.growing.dlsrpc.core.consul.{RpcDiscoveryService, RpcRegisterService}
import io.growing.dlsrpc.core.utils.ServiceLoadUtil

/**
 * 测试注入和获取服务名
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 */
object TestConsulService extends App {

  private lazy val register: RpcRegisterService = ServiceLoadUtil.getProvider(classOf[RpcRegisterService])
  private lazy val discover: RpcDiscoveryService = ServiceLoadUtil.getProvider(classOf[RpcDiscoveryService])

  //单例对象有两个类名，一个是原始类，[TestGuiceModule$, TestGuiceModule] , 这里给consul的8500端口发健康检查 假装一直存活（滑稽）
  //注册到consul的服务需要自己提供一个含有/health接口的controller，作为健康检查
  //  register.registerService("Hello1", NormalServiceAddress("127.0.0.1", 8500))
  register.registerService("Hello2", new WeightServiceAddress("127.0.0.1", 8500))
  //巨坑，port无效，必须设置address的加port，而加了port后address又会报错，主要是tcp属性就已经包含port，并且覆盖前面设置的address port
  // println(discover.obtainServiceAddress("Hello1"))
  println("Hello2 = " + discover.obtainServiceAddress("Hello2"))
  //8500只是健康监测，实际端口是9998 在配置文件中。默认服务自己提供健康接口
}
