package io.github.dlsrpc.core.client

import java.net.SocketAddress

import io.github.dlsrpc.core.api.{AbstractChannel, Protocol}

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
  def open(messageHandler: ClientMessageHandler, socketAddress: SocketAddress, protocol: Protocol): Unit

  /**
   * 发送消息
   *
   * @param msg
   */
  def sendMessage(msg: Array[Byte]): Unit
}
