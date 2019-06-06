package io.growing.dls.server

import java.lang.reflect.Method

import com.typesafe.scalalogging.LazyLogging
import io.growing.dls.meta.{RpcRequest, RpcResponse}
import io.growing.dls.{SendMessage, Serializer}

/**
 * 服务端消息处理实现
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
class ServerMessageHandlerImpl extends ServerMessageHandler with LazyLogging {

  private[this] var serializer: Serializer = _
  private[this] var serviceBean: Any = _
  private[this] var channel: ServerChannel = _

  def this(serviceBean: Any, serializer: Serializer, channel: ServerChannel) {
    this()
    this.serviceBean = serviceBean
    this.serializer = serializer
    this.channel = channel
  }

  override def receiveAndProcessor(request: Array[Byte], receiveMessage: SendMessage): Unit = {
    //接口消息并反序列化解码拿到真正的请求
    val rpcRequest: RpcRequest = serializer.deserializer(request, classOf[RpcRequest])
    val rpcResponse = new RpcResponse
    rpcResponse.setRequestId(rpcRequest.getRequestId)
    var method: Method = null
    try {
      //通过方法名称和参数类型确定一个方法
      method = serviceBean.getClass.getMethod(rpcRequest.getMethodName, rpcRequest.getParameterTypes: _*)
    } catch {
      case e: Exception =>
        logger.warn("Client send msg is fail : {}", e)
        rpcResponse.setError(e)
    }
    if (method != null) {
      try {
        //使用得当的方法进行调用，并传入接收到参数列表（可变长参数使用:_*）
        val ret = method.invoke(serviceBean, rpcRequest.getParameters: _*)
        rpcResponse.setResult(ret)
      }
      catch {
        case e: Exception =>
          logger.warn("Method : {}  invoke! fail : {}", method.getName, e)
          rpcResponse.setError(e)
      }
    }
    //序列化并返回数据给客户端
    receiveMessage.sendMsg(serializer.serializer(rpcResponse))
  }
}
