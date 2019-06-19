package io.growing.dlsrpc.core.server

import java.util.concurrent.Executor

import com.google.inject.Singleton
import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.config.DlsRpcConfiguration
import io.growing.dlsrpc.common.metadata.NormalServiceAddress
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
                       , rpc: RPCRegisterService) extends LazyLogging {

  //传输协议，未使用
  @volatile
  private[this] var protocol: Protocol = _
  //服务绑定的端口
  @volatile
  private[this] var port: Int = _
  //需要发布rpc的服务，一条channel可以发布多个服务，但是这里目前采用这种方案，一条channel对应一个服务
  //注册和发现服务写好后会改成多个
  @volatile
  private[this] var serviceBeans: Seq[AnyRef] = _
  //服务端任务执行器，使用缓存线程池
  private[this] final lazy val executor: Executor = ExecutorBuilder.executorBuild("dlsRpc-thread-executor-%d", daemon = true)

  protected[server] def setPort(port: Int): Server = {
    this.port = port
    this
  }

  def setBeans(serviceBeans: Seq[AnyRef]): Server = {
    if (this.serviceBeans != null && this.serviceBeans.nonEmpty) {
      this.serviceBeans = this.serviceBeans ++ serviceBeans
    } else {
      this.serviceBeans = serviceBeans
    }
    this
  }

  //对于通道错误不予捕获，任务服务没有进行下去的必要
  def start(): Unit = {
    IsCondition.conditionException(serviceBeans == null || !port.isValidInt || port < 0, "params error")
    //注入进的消息处理器并不知发布哪个服务
    messageHandler.setProcessBeans(serviceBeans)
    serverChannel.openServerChannel(port, executor, protocol, messageHandler)
    logger.info("Server start port : {}", port)
    //默认将可见的所有类注册到本地的consul并暴露127.0.0.1:8080
    rpc.initRegisterService(NormalServiceAddress(DlsRpcConfiguration.WEB_SERVER_IP, port))
  }

  //关闭时强制GC
  protected[server] def shutdown(): Unit = {
    try serverChannel.shutdown()
    finally System.gc()
  }
}
