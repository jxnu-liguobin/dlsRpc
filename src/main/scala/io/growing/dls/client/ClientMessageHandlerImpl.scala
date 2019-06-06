package io.growing.dls.client

import java.util.concurrent.{BlockingQueue, ConcurrentMap, LinkedBlockingQueue, TimeUnit}

import com.google.common.collect.Maps
import com.typesafe.scalalogging.LazyLogging
import io.growing.dls.Serializer
import io.growing.dls.exception.RPCException
import io.growing.dls.meta.{RpcRequest, RpcResponse}

/**
 * 客户端消息处理器实现
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-04
 */
class ClientMessageHandlerImpl extends ClientMessageHandler with LazyLogging {

  //序列化
  private[this] var serializer: Serializer = _
  //客户端通道
  private[this] var channel: ClientChannel = _
  //超时时间
  private[this] val TIME_AWAIT: Int = 30 * 1000
  //记录请求id和调用返回
  private[this] var mapCallBack: ConcurrentMap[Long, BlockingQueue[RpcResponse]] = _

  def this(serializer: Serializer, channel: ClientChannel) {
    this()
    this.serializer = serializer
    this.channel = channel
    mapCallBack = Maps.newConcurrentMap()
  }

  @throws[Exception]
  override def receiveAndProcessor(request: Array[Byte]): Unit = {
    //反序列化收到的消息
    val rpcResponse = this.serializer.deserializer(request, classOf[RpcResponse])
    if (rpcResponse != null && rpcResponse.requestId > 0) {
      val queue: BlockingQueue[RpcResponse] = mapCallBack.get(rpcResponse.getRequestId)
      queue.add(rpcResponse)
      mapCallBack.remove(rpcResponse.getRequestId)
    } else logger.warn("ReceiveAndProcessor not found data getRequestId : {}", rpcResponse.requestId)
  }

  @throws[Exception]
  override def sendAndProcessor(rpcRequest: RpcRequest): AnyRef = {
    val requestMsg = this.serializer.serializer(rpcRequest)
    val queue = new LinkedBlockingQueue[RpcResponse]
    mapCallBack.put(rpcRequest.getRequestId, queue)
    channel.sendMsg(requestMsg)
    val response: RpcResponse = queue.poll(TIME_AWAIT, TimeUnit.MILLISECONDS)
    if (response != null) if (response.getError != null) throw RPCException(response.getError.getMessage)
    else response.getResult
    else throw RPCException("Request wait response time await")
  }
}
