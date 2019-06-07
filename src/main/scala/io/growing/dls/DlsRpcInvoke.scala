package io.growing.dls

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import io.growing.dls.client.ClientBuilder.builderWithClass
import io.growing.dls.server.ServerBuilder

/**
 * 封装服务注册和获取，一个服务对应一个channel端口
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-07
 */
object DlsRpcInvoke {

  def publishService(port: Int, serviceBean: Any): Unit = {
    val server = ServerBuilder.buildWithPort(port).publishService(serviceBean).build
    server.start()
    TimeUnit.SECONDS.sleep(1000)
    //防止还没启动就停了
    server.shutdown()
  }

  def obtainService[T](host: String, por: Int, target: Class[T]): T = {
    val serviceAddress: InetSocketAddress = InetSocketAddress.createUnresolved(host, por)
    builderWithClass(target).bindingAddress(serviceAddress).build
  }

}
