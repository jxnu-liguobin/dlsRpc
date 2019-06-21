package io.growing.dlsrpc.core.server

import java.util.{List => JList}

import io.growing.dlsrpc.common.utils.{ImplicitUtils, IsCondition}
import io.growing.dlsrpc.core.api.Protocol
import io.growing.dlsrpc.core.utils.ServiceLoadUtil

/**
 * 服务端建造器
 *
 * @author 梦境迷离
 * @version 1.2, 2019-06-05
 */
class ServerBuilder private() {

  //注入服务端
  private[this] final lazy val server: Server = ServiceLoadUtil.getProvider(classOf[Server])
  //传输协议，目前没使用
  @volatile
  private[this] final var protocol: Protocol = _
  //服务端口
  @volatile
  private[this] final var port: Int = _
  //需要发布rpc的服务
  @volatile
  private[this] final var serviceBeans: Seq[AnyRef] = _

  //允许覆盖初始化传进来的值
  def bindPort(port: Int): ServerBuilder = {
    this.port = port
    this
  }

  def publishServices(serviceBeans: Seq[AnyRef]): ServerBuilder = {
    if (this.serviceBeans != null && this.serviceBeans.nonEmpty) {
      this.serviceBeans = this.serviceBeans ++ serviceBeans
    } else {
      this.serviceBeans = serviceBeans
    }
    this
  }

  /**
   * 兼容 Java (自动转换可能丢失类型)
   *
   * @param serviceBeans
   * @return
   */
  def publishServices(serviceBeans: JList[Object]): ServerBuilder = {
    val sbs = ImplicitUtils.jListToSeq[Object](serviceBeans)
    publishServices(sbs)
  }

  //这里主要是设置端口并发布服务，之所以需要改为注入是为了后面拓展发布多服务
  //build后不能再修改端口
  def build: Server = {
    IsCondition.conditionException(serviceBeans == null || !port.isValidInt || port < 0, "params error")
    server.setBeans(serviceBeans)
    server.setPort(port)
    server
    //protocol = ServiceLoadUtil.getProvider(Protocol.class);
  }

  def setTransportProtocol(protocol: Protocol): ServerBuilder = {
    this.protocol = protocol
    this
  }

  def stopServer: Unit = this.server.shutdown()
}

object ServerBuilder {
  //实例化建造器，直到调用build才会真正绑定和发布服务
  def buildWithPort(port: Int): ServerBuilder = new ServerBuilder().bindPort(port)
}