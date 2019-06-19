package io.growing.dlsrpc.core.server

import com.google.inject.ImplementedBy
import io.growing.dlsrpc.core.api.SendMessage

/**
 * 服务端消息处理器顶级接口
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-05
 */
@ImplementedBy(classOf[ServerMessageHandlerImpl])
trait ServerMessageHandler {

  /**
   * 接收并处理消息
   *
   * @param request        来自客户端的请求消息
   * @param receiveMessage 发送消息的实现
   */
  @throws[Exception]
  def processor(request: Array[Byte], receiveMessage: SendMessage): Unit

  /**
   * 设置需要处理的服务
   *
   * @param beans
   */
  def setProcessBeans(beans: Seq[AnyRef])

}
