package io.growing.dlsrpc.core.server

import io.growing.dlsrpc.common.utils.IsCondition
import io.growing.dlsrpc.core.api.Protocol
import io.growing.dlsrpc.core.utils.ServiceLoadUtil

/**
 * 服务端建造器
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-05
 */
class ServerBuilder private() {

  //注入服务端
  private[this] lazy val server: Server = ServiceLoadUtil.getProvider(classOf[Server])
  //传输协议，目前没使用
  private[this] var protocol: Protocol = _
  //服务端口
  private[this] var port: Int = _
  //需要发布rpc的服务
  private[this] var serviceBean: Any = _

  def bindPort(port: Int): ServerBuilder = {
    this.port = port
    this
  }

  def publishService(serviceBean: Any): ServerBuilder = {
    this.serviceBean = serviceBean
    this
  }

  //这里主要是设置端口并发布服务，之所以需要改为注入是为了后面拓展发布多服务
  def build: Server = {
    IsCondition.conditionException(serviceBean == null || !port.isValidInt || port < 0, "params error")
    server.setBean(serviceBean)
    server.setPort(port)
    server
    //protocol = ServiceLoadUtil.getProvider(Protocol.class);
  }
}

object ServerBuilder {
  //实例化建造器，直到调用build才会真正绑定和发布服务
  def buildWithPort(port: Int): ServerBuilder = new ServerBuilder().bindPort(port)
}