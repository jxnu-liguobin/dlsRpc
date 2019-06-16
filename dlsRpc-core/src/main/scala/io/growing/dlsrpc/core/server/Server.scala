package io.growing.dlsrpc.core.server

import java.util.concurrent.Executor

import com.google.inject.Singleton
import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.utils.IsCondition
import io.growing.dlsrpc.core.api.{Protocol, Serializer}
import io.growing.dlsrpc.core.rpc.RPCRegisterService
import io.growing.dlsrpc.core.utils.ExecutorBuilder
import javax.inject.Inject

/**
 *
 * 内部服务器端基本实现
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-05
 * @param serializer     序列化
 * @param serverChannel  服务通道
 * @param messageHandler 消息处理
 */
@Singleton
class Server @Inject()(serializer: Serializer, serverChannel: ServerChannel, messageHandler: ServerMessageHandler
                       , registerService: RPCRegisterService) extends LazyLogging {

  //传输协议，未使用
  @volatile
  private[this] var protocol: Protocol = _
  //服务绑定的端口
  @volatile
  private[this] var port: Int = _
  //需要发布rpc的服务，一条channel可以发布多个服务，但是这里目前采用这种方案，一条channel对应一个服务
  //注册和发现服务写好后会改成多个
  @volatile
  private[this] var serviceBean: Any = _
  //服务端任务执行器，使用缓存线程池
  private[this] final lazy val executor: Executor = ExecutorBuilder.executorBuild("dlsRpc-thread-executor-%d", daemon = true)

  def setPort(port: Int): Server = {
    this.port = port
    this
  }

  def setBean(serviceBean: Any): Server = {
    this.serviceBean = serviceBean
    this
  }

  def setProtocol(protocol: Protocol) = {
    this.protocol = protocol
    this
  }

  //对于通道错误不予捕获，任务服务没有进行下去的必要
  def start(): Unit = {
    IsCondition.conditionException(serviceBean == null || !port.isValidInt || port < 0, "params error")
    //注入进的消息处理器并不知发布哪个服务
    messageHandler.setProcessBean(serviceBean)
    serverChannel.openServerChannel(port, executor, protocol, messageHandler)
    logger.info("Server start port : {}", port)
    //TODO 启动服务的时候开始初始化注册所有有注解的服务
    //      rpc.initRegisterService(Constants.CONSUL_ADDRESS)
  }

  //关闭时强制GC
  protected[server] def shutdown(): Unit = {
    try serverChannel.shutdown()
    finally System.gc()
  }
}
