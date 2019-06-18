package io.growing.dlsrpc.core.client

import java.lang.reflect.Proxy.newProxyInstance
import java.lang.reflect.{InvocationHandler, Method}
import java.net.{InetSocketAddress, SocketAddress}
import java.util.concurrent.atomic.AtomicLong

import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.config.DlsRpcConfiguration._
import io.growing.dlsrpc.common.enums.ProxyType
import io.growing.dlsrpc.common.metadata.{RpcRequest, ServiceAddress}
import io.growing.dlsrpc.common.utils.{IsCondition, SubClassUtils, SuperClassUtils}
import io.growing.dlsrpc.core.api.Protocol
import io.growing.dlsrpc.core.rpc.RPCDiscoveryService
import io.growing.dlsrpc.core.utils.ServiceLoadUtil
import net.sf.cglib.proxy.{Enhancer, MethodInterceptor, MethodProxy}

/**
 * 内部客户端基本实现
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-04
 */
class Client[Builder <: Client[_, _], T] protected(clientClass: Class[T]) extends LazyLogging {

  //获得客户端通道
  private[this] lazy val clientChannel: ClientChannel = ServiceLoadUtil.getProvider(classOf[ClientChannel])
  //服务发现
  private[this] lazy val rpc: RPCDiscoveryService = ServiceLoadUtil.getProvider(classOf[RPCDiscoveryService])
  //传输协议
  @volatile
  private[this] var protocol: Protocol = _
  //HTT2
  //  private[this] lazy val protocol: Protocol = ServiceLoadUtil.getProvider(classOf[Protocol])
  //服务端地址
  @volatile
  private[this] var socketAddress: SocketAddress = _
  //线程安全的自增请求id
  private[this] lazy val atomicLong: AtomicLong = new AtomicLong(REQUEST_START_VALUE)
  //消息处理器
  private[this] lazy val messageHandler: ClientMessageHandler = ServiceLoadUtil.getProvider(classOf[ClientMessageHandler])
  //调用服务的实现的接口（JDK代理，必须要有实现接口）
  //  @volatile
  //  private[this] var clientClass: Class[T] = _
  //this.protocol = ServiceLoadUtil.getProvider(Protocol.class);

  def getClientChannel: ClientChannel = this.clientChannel

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
    //服务发现

    this.socketAddress = InetSocketAddress.createUnresolved(host, port)
    this.asInstanceOf[Builder]
  }


  //链接到默认服务中心，获得绑定的实际服务地址
  def linkToCenter: Builder = {
//    var classs: String = null
//    //是接口就获取子类，不是接口就直接使用类名
//    if (clientClass.isInterface) {
//      classs = SubClassUtils.getSubClassName(clientClass)
//    } else {
//      classs = clientClass.getSimpleName
//    }
    //服务发现
    val serviceAddress: ServiceAddress = rpc.obtainServiceAddress(clientClass.getSimpleName)
    IsCondition.conditionException(serviceAddress == null, "can't find any service address")
    this.socketAddress = InetSocketAddress.createUnresolved(serviceAddress.getIp, serviceAddress.getPort)
    this.asInstanceOf[Builder]
  }

  //不予捕获通道造成错误
  private[client] def start(): Unit = {
    //客户端通道开启
    clientChannel.open(messageHandler, socketAddress, protocol)
    logger.info("clientChannel start success ")
  }

  //创建动态代理并发送请求，获取服务端的结果。
  @throws[Exception]
  private[client] def proxy[T]: T = {
    IsCondition.conditionException(clientClass == null, "param error")
    val clientInvocationHandler: InvocationHandler = (proxy, method, args) => {
      //执行方法时被调用
      def invoke(proxy: Any, method: Method, args: Array[_ <: Any]) = {
        val request: RpcRequest = new RpcRequest
        request.setRequestId(atomicLong.incrementAndGet)
        request.setClassName(clientClass.getSimpleName)
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

  //不知道为什么这个抽出去会出问题
  private[this] class ClientCglibProxy extends MethodInterceptor {
    //执行方法时，实际是去调用远程的方法并获取结果
    @throws[Throwable]
    override def intercept(o: scala.AnyRef, method: Method, objects: Array[AnyRef], methodProxy: MethodProxy): AnyRef = {
      val request: RpcRequest = new RpcRequest
      request.setRequestId(atomicLong.incrementAndGet)
      request.setClassName(o.getClass.getSimpleName)
      request.setMethodName(method.getName)
      request.setParameterTypes(method.getParameterTypes)
      request.setParameters(objects)
      messageHandler.sendProcessor(request)
    }
  }

  //cglib代理构造代理对象
  @throws[Exception]
  private[client] def cglibProxy[T]: T = {
    IsCondition.conditionException(clientClass == null, "param error")
    val daoProxy = new ClientCglibProxy
    val enhancer = new Enhancer
    enhancer.setCallback(daoProxy)
    enhancer.setSuperclass(clientClass)
    enhancer.create.asInstanceOf[T]
  }

  //关闭时强制GC
  protected[client] def shutdown(): Unit = {
    try clientChannel.shutdown()
    finally {
      System.gc()
      System.exit(0)
    }
  }
}
