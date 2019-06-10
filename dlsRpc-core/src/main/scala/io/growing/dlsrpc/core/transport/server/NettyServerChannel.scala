package io.growing.dlsrpc.core.transport.server

import java.util.concurrent.Executor

import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.utils.IsCondition
import io.growing.dlsrpc.core.api.Protocol
import io.growing.dlsrpc.core.server.{ServerChannel, ServerMessageHandler}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.{Channel, ChannelFuture, EventLoopGroup}


/**
 * Netty服务端通道
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
class NettyServerChannel extends ServerChannel with LazyLogging {

  //控制流、多线程处理、并发
  //一个EventLoopGroup包含一个或者多个EventLoop
  // 一个EventLoop在它的生命周期内只和一个Thread绑定
  //所有由EventLoop处理得I/O事件都将在它专有的Thread上处理
  //一个Channel在它的生命周期内只注册一个EventLoop
  //一个EventLoop可能会被分配给一个或多个Channel
  private[this] lazy final val bossGroup: EventLoopGroup = new NioEventLoopGroup
  private[this] lazy final val workerGroup: EventLoopGroup = new NioEventLoopGroup
  private[this] var channel: Channel = _

  override def openServerChannel(port: Int, executor: Executor, protocol: Protocol, messageHandler: ServerMessageHandler): Unit = {
    //异步通知
    val nettyServerChannelFuture: ChannelFuture = ServerChannelBuilder.build(bossGroup, workerGroup,
      new ServerChannelInitializer(executor, messageHandler), port)

    try {
      nettyServerChannelFuture.await
    }
    catch {
      case ex: InterruptedException =>
        Thread.currentThread.interrupt()
        logger.warn("Start fail : {}", ex)
        throw new RuntimeException("Interrupted waiting for bind")
    }
    IsCondition.conditionWarn(!nettyServerChannelFuture.isSuccess, s"Start fail : {${nettyServerChannelFuture.cause}")
    IsCondition.conditionException(!nettyServerChannelFuture.isSuccess, "Failed to bind", nettyServerChannelFuture.cause())
    channel = nettyServerChannelFuture.channel
  }

  override def shutdown(): Unit = {
    if (IsCondition.conditionWarn(channel == null || !channel.isOpen)) return
    try {
      channel.close
    } catch {
      case e: Exception =>
        logger.warn("Close NettyServerChannel fail : {}", e)
    } finally workerGroup.shutdownGracefully
  }
}
