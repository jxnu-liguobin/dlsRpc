package io.growing.dlsrpc.core.client

import com.google.inject.ImplementedBy
import io.growing.dlsrpc.common.metadata.RpcRequest


/**
 * 客户端消息处理器顶级接口
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-04
 */
@ImplementedBy(classOf[ClientMessageHandlerImpl])
trait ClientMessageHandler {

  /**
   * 接收消息处理
   *
   * @param request
   * @throws Exception 可能是序列化
   */
  @throws[Exception]
  def receiveProcessor(request: Array[Byte]): Unit

  /**
   * 发现消息时处理
   *
   * @param rpcRequest
   * @throws Exception 可能是序列化或线程中断
   * @return
   */
  @throws[Exception]
  def sendProcessor(rpcRequest: RpcRequest): AnyRef
}
