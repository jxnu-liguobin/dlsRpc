package io.growing.dls.client

import java.util.concurrent.{BlockingQueue, ConcurrentMap, LinkedBlockingQueue, TimeUnit}

import com.google.common.collect.Maps
import com.typesafe.scalalogging.LazyLogging
import io.growing.dls.Serializer
import io.growing.dls.meta.{RpcRequest, RpcResponse}
import io.growing.dls.utils.{Constants, IsCondition}

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
  private[this] final lazy val TIME_AWAIT: Int = Constants.TIME_AWAIT
  //记录请求id和调用返回
  private[this] final lazy val mapCallBack: ConcurrentMap[Long, BlockingQueue[RpcResponse]] = Maps.newConcurrentMap()

  def this(serializer: Serializer, channel: ClientChannel) {
    this()
    this.serializer = serializer
    this.channel = channel
  }

  @throws[Exception]
  override def receiveProcessor(request: Array[Byte]): Unit = {
    //反序列化收到的消息
    val rpcResponse = this.serializer.deserializer(request, classOf[RpcResponse])
    IsCondition.conditionWarn(rpcResponse == null || rpcResponse.getRequestId < 1,
      s"ReceiveAndProcessor not found data getRequestId : {${rpcResponse.getRequestId}}") match {
      case false => {
        //获取该请求id的返回信息队列
        val queue: BlockingQueue[RpcResponse] = mapCallBack.get(rpcResponse.getRequestId)
        //将返回信息保存到队列
        queue.add(rpcResponse)
        //从回调中删除该请求
        mapCallBack.remove(rpcResponse.getRequestId)
      }
      case true => {
        //TODO
      }
    }
  }

  //获取代理对象的时候调用该方法发送请求
  @throws[Exception]
  override def sendProcessor(rpcRequest: RpcRequest): AnyRef = {
    //序列化
    val requestMsg = this.serializer.serializer(rpcRequest)
    val queue = new LinkedBlockingQueue[RpcResponse]
    //保存请求信息
    mapCallBack.put(rpcRequest.getRequestId, queue)
    //发送消息
    channel.sendMessage(requestMsg)
    //取出返回信息 30S超时时间
    val response: RpcResponse = queue.poll(TIME_AWAIT, TimeUnit.MILLISECONDS)
    IsCondition.conditionException(response == null, "Request wait response time await")
    IsCondition.conditionException(response.getError != null, cause = response.getError)
    response.getResult
  }
}
