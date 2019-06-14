package io.growing.dlsrpc.core.client

import java.util.concurrent._

import com.google.inject.Singleton
import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.config.DlsRpcConfiguration._
import io.growing.dlsrpc.common.metadata.{RpcRequest, RpcResponse}
import io.growing.dlsrpc.common.utils.IsCondition
import io.growing.dlsrpc.core.api.Serializer
import javax.inject.Inject


/**
 *
 * 客户端消息处理器实现
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-04
 * @param serializer 序列化
 * @param channel    实际发送消息的通道
 */
@Singleton
class ClientMessageHandlerImpl @Inject()(serializer: Serializer, channel: ClientChannel)
  extends ClientMessageHandler with LazyLogging {

  //记录请求id和调用返回
  private[this] final lazy val mapCallBack: ConcurrentMap[Long, BlockingQueue[RpcResponse]] = new ConcurrentHashMap

  @throws[Exception]
  override def receiveProcessor(request: Array[Byte]): Unit = {
    //反序列化收到的消息
    val rpcResponse = serializer.deserializer(request, classOf[RpcResponse])
    IsCondition.conditionWarn(rpcResponse == null || rpcResponse.getRequestId < 1,
      s"ReceiveAndProcessor not found data getRequestId : {${rpcResponse.getRequestId}}") match {
      case false => {
        try {
          //获取该请求id的返回信息队列
          val queue: BlockingQueue[RpcResponse] = mapCallBack.get(rpcResponse.getRequestId)
          //将返回信息保存到队列
          queue.add(rpcResponse)
          //从回调中删除该请求
          mapCallBack.remove(rpcResponse.getRequestId)
        } catch {
          case e: Exception => {
            logger.error("Client receive message fail {} ", e.getMessage)
          }
        }
      }
      case true => {
        //TODO
      }
    }
  }

  //获取代理对象的时候调用该方法发送请求
  @throws[Exception]
  override def sendProcessor(rpcRequest: RpcRequest): AnyRef = {
    var response: RpcResponse = null
    try {
      //序列化
      val requestMsg = serializer.serializer(rpcRequest)
      val queue = new LinkedBlockingQueue[RpcResponse]
      //保存请求信息
      mapCallBack.put(rpcRequest.getRequestId, queue)
      //发送消息
      channel.sendMessage(requestMsg)
      //取出返回信息 30S超时时间
      response = queue.poll(TIME_AWAIT, TimeUnit.MILLISECONDS)
    } catch {
      case e: Exception => {
        logger.error("Client send message fail {} ", e.getMessage)
      }
    }
    IsCondition.conditionException(response == null, "Request wait response time await")
    IsCondition.conditionException(response.getError != null, cause = response.getError)
    response.getResult
  }
}
