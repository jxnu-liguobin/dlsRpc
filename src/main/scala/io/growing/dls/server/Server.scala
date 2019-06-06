package io.growing.dls.server

import java.io.IOException
import java.util.concurrent.Executor

import com.typesafe.scalalogging.LazyLogging
import io.growing.dls.exception.RPCException
import io.growing.dls.utils.ExecutorBuilder
import io.growing.dls.{Protocol, Serializer}

/**
 * 内部服务器端基本实现
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
class Server extends LazyLogging {

  //服务端通道
  private[this] var serverChannel: ServerChannel = _
  //序列化
  private[this] var serializer: Serializer = _
  //传输协议
  private[this] var protocol: Protocol = _
  //服务绑定的端口
  private[this] var port: Int = 0
  //需要发布rpc的服务，一条channel可以发布多个服务，但是这里不采用这种方案，一条channel对应一个服务
  private[this] var serviceBean: Any = _
  //服务端消息处理器
  private[this] var messageHandler: ServerMessageHandler = _
  //服务端任务执行器
  private[this] var executor: Executor = _

  def this(serverChannel: ServerChannel, serializer: Serializer, protocol: Protocol, port: Int, serviceBean: Any) {
    this()
    this.executor = ExecutorBuilder.executorBuild("dlsRpc-business-executor-%d", daemon = true)
    this.serviceBean = serviceBean
    this.serverChannel = serverChannel
    this.serializer = serializer
    this.port = port
    this.protocol = protocol
    this.messageHandler = new ServerMessageHandlerImpl(serviceBean, serializer, serverChannel)
  }

  def start(): Unit = {
    try {
      serverChannel.start(port, executor, protocol, messageHandler)
      logger.info("Server start port : {}", port)
    } catch {
      case e: IOException =>
        throw new RPCException("serverChannel init fail : {}", e)
    }
  }

  def shutdown(): Unit = {
    serverChannel.shutdown()
  }
}
