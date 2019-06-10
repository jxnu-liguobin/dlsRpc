package io.growing.dlsrpc.core.client

import java.io.IOException
import java.lang.reflect.Proxy.newProxyInstance
import java.lang.reflect.{InvocationHandler, Method}
import java.net.{InetSocketAddress, SocketAddress}
import java.util.concurrent.atomic.AtomicLong

import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.exception.RPCException
import io.growing.dlsrpc.common.metadata.RpcRequest
import io.growing.dlsrpc.common.utils.{Constants, IsCondition}
import io.growing.dlsrpc.core.api.Protocol
import io.growing.dlsrpc.core.utils.ServiceLoadUtil

import scala.util.Try

/**
 * 内部客户端基本实现
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-04
 */
class Client[Builder <: Client[_, _], T] protected() extends LazyLogging {

  //获得客户端通道
  private[this] lazy val clientChannel: ClientChannel = ServiceLoadUtil.getProvider(classOf[ClientChannel])
  //传输协议
  private[this] var protocol: Protocol = _
  //  private[this] lazy val protocol: Protocol = ServiceLoadUtil.getProvider(classOf[Protocol])
  //服务端地址
  private[this] var socketAddress: SocketAddress = _
  //线程安全的自增请求id
  private[this] lazy val atomicLong: AtomicLong = new AtomicLong(Constants.REQUEST_START_VALUE)
  //消息处理器
  private[this] lazy val messageHandler: ClientMessageHandler = ServiceLoadUtil.getProvider(classOf[ClientMessageHandler])
  //调用服务的实现的接口（JDK代理，必须要有实现接口）
  private[this] var clientClass: Class[T] = _
  //this.protocol = ServiceLoadUtil.getProvider(Protocol.class);

  def setClientClass(clientClass: Class[T]): Client[_, _] = {
    this.clientClass = clientClass
    this
  }

  def setTransportProtocol(protocol: Protocol): Client[_, _] = {
    this.protocol = protocol
    this
  }

  def linkToAddress(socketAddress: SocketAddress): Builder = {
    this.socketAddress = socketAddress
    this.asInstanceOf[Builder]
  }

  def linkToAddress(host: String, port: Int): Builder = {
    IsCondition.conditionException(host == null, "host can't be empty")
    IsCondition.conditionException(port < 1, "port can't less than 1")
    this.socketAddress = InetSocketAddress.createUnresolved(host, port)
    this.asInstanceOf[Builder]
  }

  private[client] def start(): Unit = {
    try {
      //客户端通道开启
      clientChannel.open(messageHandler, socketAddress, protocol)
      logger.info("clientChannel start success ")
    }
    catch {
      case e: IOException =>
        throw new RPCException("clientChannel init fail", e)
    }
  }

  /**
   * 创建动态代理并发送请求，获取服务端的结果。
   *
   * @return 代理对象
   */
  private[client] def getClientProxy: T = {

    IsCondition.conditionException(clientClass == null, "param error")

    val clientInvocationHandler: InvocationHandler = (proxy, method, args) => {

      //执行方法时被调用
      def invoke(proxy: Any, method: Method, args: Array[_ <: Object]) = {
        val request: RpcRequest = new RpcRequest
        request.setRequestId(atomicLong.incrementAndGet)
        request.setClassName(clientClass.getName)
        request.setMethodName(method.getName)
        request.setParameterTypes(method.getParameterTypes)
        request.setParameters(args)
        val result: AnyRef = messageHandler.sendProcessor(request)
        result
      }

      invoke(proxy, method, args)
    }
    //根据JDK代理使用反射获得该接口的实现类的对象
    newProxyInstance(classOf[Client[_, _]].getClassLoader, Array[Class[_]](clientClass),
      clientInvocationHandler).asInstanceOf[T]
  }

  override protected def finalize() = Try(clientChannel.shutdown())

}
