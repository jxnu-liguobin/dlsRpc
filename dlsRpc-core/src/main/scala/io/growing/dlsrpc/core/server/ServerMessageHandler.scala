package io.growing.dlsrpc.core.server

import io.growing.dlsrpc.core.api.SendMessage

/**
 * 服务端消息处理器顶级接口
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
trait ServerMessageHandler {


  /**
   * 接收并处理消息
   *
   * @param request        来自客户端的请求消息
   * @param receiveMessage 发送消息的实现
   */
  def processor(request: Array[Byte], receiveMessage: SendMessage): Unit

}
