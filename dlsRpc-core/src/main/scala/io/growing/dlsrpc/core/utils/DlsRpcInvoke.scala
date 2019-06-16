package io.growing.dlsrpc.core.utils

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import io.growing.dlsrpc.core.client.ClientBuilder
import io.growing.dlsrpc.core.client.ClientBuilder._
import io.growing.dlsrpc.core.server.ServerBuilder
import io.growing.dlsrpc.core.server.ServerBuilder._

/**
 * 封装服务注册和获取，一个服务对应一个channel端口
 *
 * 后面要使用服务注册发现，并支持自动注册，支持多个服务
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-07
 */
object DlsRpcInvoke {


  /**
   * 直接发布服务
   *
   * @param port
   * @param serviceBean
   */
  def publishService(port: Int, serviceBean: Any): Unit = {
    val server = buildWithPort(port).publishService(serviceBean).build
    server.start()
    TimeUnit.SECONDS.sleep(1000)
    //防止还没启动就停了
    //    server.shutdown()
  }


  /**
   * 直接取得代理对象
   *
   * @param host
   * @param port
   * @param target
   * @tparam T
   * @return
   */
  def obtainService[T](host: String, port: Int, target: Class[T]): T = {
    val serviceAddress: InetSocketAddress = InetSocketAddress.createUnresolved(host, port)
    builderWithClass(target).linkToAddress(serviceAddress).build
  }


  /**
   * 获得client建造
   *
   * @param host
   * @param port
   * @param target
   * @tparam T
   * @return
   */
  def getClientBuilder[T](host: String, port: Int, target: Class[T]): ClientBuilder[T] = {
    val serviceAddress: InetSocketAddress = InetSocketAddress.createUnresolved(host, port)
    builderWithClass(target).linkToAddress(serviceAddress)
  }


  /**
   * 获得server建造
   *
   * @param port
   * @param serviceBean
   * @tparam T
   * @return
   */
  def getServerBuilder[T](port: Int, serviceBean: Any): ServerBuilder = {
    buildWithPort(port).publishService(serviceBean)
  }

}
