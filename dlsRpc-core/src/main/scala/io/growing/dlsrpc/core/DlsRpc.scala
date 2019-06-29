package io.growing.dlsrpc.core

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit
import java.util.{List => JList}

import io.growing.dlsrpc.common.utils.ImplicitUtils.jListToSeq
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
 * @version 1.2, 2019-06-07
 */
object DlsRpc {

  /**
   * 直接发布服务
   *
   * @param port
   * @param serviceBean
   */
  @deprecated
  def publishService(port: Int, serviceBean: AnyRef): Unit = {
    val server = buildWithPort(port).publishServices(Seq(serviceBean)).build
    server.start()
    TimeUnit.SECONDS.sleep(1000)
    //防止还没启动就停了
    //    server.shutdown()
  }

  /**
   * 发布所有服务
   *
   * @param port
   * @param serviceBeans
   */
  @deprecated
  def publishServices(port: Int, serviceBeans: Seq[AnyRef]): Unit = {
    val server = buildWithPort(port).publishServices(serviceBeans).build
    server.start()
    TimeUnit.SECONDS.sleep(1000)
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
  @deprecated
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
  @deprecated
  def getClientBuilder[T](host: String, port: Int, target: Class[T]): ClientBuilder[T] = {
    val serviceAddress: InetSocketAddress = InetSocketAddress.createUnresolved(host, port)
    builderWithClass(target).linkToAddress(serviceAddress)
  }

  /**
   * 使用默认的服务中心
   *
   * @param target 需要调用的服务的类名
   * @tparam T
   * @return
   */
  def getClientBuilder[T](target: Class[T]): ClientBuilder[T] = {
    builderWithClass(target).linkToCenter
  }

  /**
   * 获得server建造
   *
   * @param port
   * @param serviceBean
   * @return
   */
  def getServerBuilder(port: Int, serviceBean: AnyRef): ServerBuilder = {
    buildWithPort(port).publishServices(Seq(serviceBean))
  }

  /**
   * 发布多个服务（暴露）
   *
   * Scala API
   *
   * @param port WEB端口
   * @param serviceBeans
   * @return
   */
  def getServerBuilder(port: Int, serviceBeans: Seq[AnyRef]): ServerBuilder = {
    buildWithPort(port).publishServices(serviceBeans)
  }

  /**
   * Java api
   *
   * @param port
   * @param serverBeans 即时List可以被自动转换为Seq 但是消息处理时的类型会出问题，所以这里手动转换
   * @return
   */
  def getServerBuilder(port: Int, serverBeans: JList[Object]): ServerBuilder = {
    getServerBuilder(port, jListToSeq[Object](serverBeans))
  }

}
