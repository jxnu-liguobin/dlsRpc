package io.growing.dlsrpc.core.server

import java.lang.reflect.Method

import com.google.inject.Singleton
import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.metadata.{RpcRequest, RpcResponse}
import io.growing.dlsrpc.common.utils.{IsCondition, SuperClassUtils}
import io.growing.dlsrpc.core.api.{SendMessage, Serializer}
import javax.inject.Inject
import net.sf.cglib.reflect.FastClass

/**
 * 服务端消息处理实现
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-05
 */
@Singleton
class ServerMessageHandlerImpl @Inject()(serializer: Serializer, channel: ServerChannel) extends ServerMessageHandler with LazyLogging {

  //需要处理的服务，实际使用反射调用这里只需要class文件名，不需要bean
  private[this] var serviceBean: Any = _

  //手动选择bean
  def setProcessBean(bean: Any) = {
    this.serviceBean = bean
  }


  @throws[Exception]
  override def processor(request: Array[Byte], receiveMessage: SendMessage): Unit = {
    IsCondition.conditionException(this.serviceBean == null, "bean can't be null")
    //接口消息并反序列化解码拿到真正的请求
    val rpcRequest: RpcRequest = serializer.deserializer(request, classOf[RpcRequest])
    val rpcResponse = new RpcResponse
    rpcResponse.setRequestId(rpcRequest.getRequestId)
    //TODO 切换调用方式
    var method: Method = null
    try {
      var ret: AnyRef = None
      SuperClassUtils.matchProxy(serviceBean.getClass) match {
        case "CGLIB" => {
          val serviceFastClass = FastClass.create(serviceBean.getClass)
          val serviceFastMethod = serviceFastClass.getMethod(rpcRequest.getMethodName, rpcRequest.getParameterTypes)
          ret = serviceFastMethod.invoke(serviceBean, rpcRequest.getParameters.asInstanceOf[Array[AnyRef]])
          logger.debug("CGLIB invoke")
        }
        case "JDK" => {
          //通过方法名称和参数类型确定一个方法
          method = serviceBean.getClass.getMethod(rpcRequest.getMethodName, rpcRequest.getParameterTypes: _*)
          ret = method.invoke(serviceBean, rpcRequest.getParameters.asInstanceOf[Array[Object]]: _*)
          logger.debug("JDK invoke")
        }
      }
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
