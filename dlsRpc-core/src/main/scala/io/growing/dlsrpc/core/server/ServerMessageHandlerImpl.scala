package io.growing.dlsrpc.core.server

import java.lang.reflect.Method

import com.google.inject.Singleton
import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.enums.ProxyType
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
  @volatile
  private[this] var serviceBeans: Seq[AnyRef] = _

  //手动选择bean
  def setProcessBeans(bean: Seq[AnyRef]) = {
    this.serviceBeans = bean
  }

  @throws[Exception]
  override def processor(request: Array[Byte], receiveMessage: SendMessage): Unit = {
    var processorBean: Any = null
    IsCondition.conditionException(this.serviceBeans == null, "bean can't be null")
    //接口消息并反序列化解码拿到真正的请求
    val rpcRequest: RpcRequest = serializer.deserializer(request, classOf[RpcRequest])
    //根据请求的类获取真实调用的bean
    for (bean <- serviceBeans) {
      val className = rpcRequest.getBeanClass
      //不是接口，可能是cglib或者使用实现类调用，那么类名与bean名是匹配的
      if (!className.isInterface && className.isInstance(bean)) {
        processorBean = bean
      } else if (className.isInterface) {
        //是接口时请求传过来的是接口名，获取该接口是否与某个bean所实现的接口匹配
        val subClass = SuperClassUtils.CheckSuperInterfaces(bean.getClass, className)
        //是接口时，先获取bean的实现接口
        if (subClass != null) {
          processorBean = bean
        }
      }
    }
    IsCondition.conditionException(processorBean == null, "bean can't be found")
    val rpcResponse = new RpcResponse
    rpcResponse.setRequestId(rpcRequest.getRequestId)
    //切换调用方式
    var method: Method = null
    try {
      var ret: AnyRef = None
      SuperClassUtils.matchProxy(processorBean.getClass) match {
        case ProxyType.CGLIB => {
          val serviceFastClass = FastClass.create(processorBean.getClass)
          val serviceFastMethod = serviceFastClass.getMethod(rpcRequest.getMethodName, rpcRequest.getParameterTypes)
          ret = serviceFastMethod.invoke(processorBean, rpcRequest.getParameters.asInstanceOf[Array[AnyRef]])
          logger.debug("CGLIB invoke")
        }
        case ProxyType.JDK => {
          //通过方法名称和参数类型确定一个方法
          method = processorBean.getClass.getMethod(rpcRequest.getMethodName, rpcRequest.getParameterTypes: _*)
          ret = method.invoke(processorBean, rpcRequest.getParameters.asInstanceOf[Array[Object]]: _*)
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
