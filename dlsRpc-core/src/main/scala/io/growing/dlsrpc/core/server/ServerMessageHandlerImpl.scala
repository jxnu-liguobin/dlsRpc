package io.growing.dlsrpc.core.server

import java.lang.reflect.Method

import com.google.inject.Singleton
import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.metadata.{RpcRequest, RpcResponse}
import io.growing.dlsrpc.common.utils.IsCondition
import io.growing.dlsrpc.core.api.{SendMessage, Serializer}
import javax.inject.Inject

/**
 * 服务端消息处理实现
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-05
 */
@Singleton
class ServerMessageHandlerImpl @Inject()(serializer: Serializer, channel: ServerChannel) extends ServerMessageHandler with LazyLogging {

  //需要处理的服务
  private[this] var serviceBean: Any = _

  //手动选择bean
  def setProcessBean(bean: Any) = {
    this.serviceBean = bean
  }

  override def processor(request: Array[Byte], receiveMessage: SendMessage): Unit = {
    IsCondition.conditionException(this.serviceBean == null, "bean can't be null")
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
    IsCondition.conditionException(method == null, "method can't be null")

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
    //序列化并返回数据给客户端
    receiveMessage.send(serializer.serializer(rpcResponse))
  }
}
