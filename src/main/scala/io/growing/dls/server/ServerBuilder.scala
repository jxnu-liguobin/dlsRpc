package io.growing.dls.server

import io.growing.dls.utils.ServiceLoadUtil
import io.growing.dls.{Protocol, Serializer}

/**
 * 服务端建造器
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
final class ServerBuilder(port: Int) {

  //通道
  private[this] var serverChannel: ServerChannel = _
  //序列化
  private[this] var serializer: Serializer = _
  //传输协议
  private[this] var protocol: Protocol = _
  //服务端口
  private[this] var p: Int = port
  //需要发布rpc的服务
  private[this] var serviceBean: Any = _

  def this(sc: ServerChannel, s: Serializer, pl: Protocol, port: Int, sb: Any) {
    this(port)
    this.serverChannel = sc
    this.serializer = s
    this.protocol = pl
    this.serviceBean = sb
  }

  def publishService(serviceBean: Any): ServerBuilder = {
    this.serviceBean = serviceBean
    this
  }

  def build: Server = {
    serverChannel = ServiceLoadUtil.getProvider(classOf[ServerChannel])
    serializer = ServiceLoadUtil.getProvider(classOf[Serializer])
    //protocol = ServiceLoadUtil.getProvider(Protocol.class);
    new Server(serverChannel, serializer, protocol, port, serviceBean)
  }
}

object ServerBuilder {
  //实例化建造器，直到调用build才会真正绑定
  def forPort(port: Int) = new ServerBuilder(port)
}