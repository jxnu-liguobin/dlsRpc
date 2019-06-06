package io.growing.dls.client

import java.net.SocketAddress

import io.growing.dls.{AbstractChannel, Protocol}

/**
 * 客户端通道接口
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-04
 */
trait ClientChannel extends AbstractChannel {


  /**
   * 打开通道
   *
   * @param messageHandler
   * @param socketAddress
   * @param protocol
   */
  def start(messageHandler: ClientMessageHandler, socketAddress: SocketAddress, protocol: Protocol): Unit


  /**
   * 发送消息
   *
   * @param msg
   */
  def sendMsg(msg: Array[Byte]): Unit
}
