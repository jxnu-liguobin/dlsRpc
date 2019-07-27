package io.github.dlsrpc.core.transport.client

import java.net.SocketAddress

import com.typesafe.scalalogging.LazyLogging
import io.github.dlsrpc.common.utils.CheckCondition
import io.github.dlsrpc.core.api.Protocol
import io.github.dlsrpc.core.client.{ClientChannel, ClientMessageHandler}
import io.github.dlsrpc.core.utils.ChannelWriteMessageUtil
import io.netty.bootstrap.Bootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.{Channel, EventLoopGroup}
import javax.inject.Inject

/**
 * Netty客户端通道
 *
 * @author 梦境迷离
 * @version 1.2, 2019-06-05
 */
class NettyClientChannel @Inject()(clientChannelInitializer: ClientChannelInitializer) extends ClientChannel with LazyLogging {

  //客户端线程池也可以贡献。channel关闭时不能关闭group
  private[this] lazy final val workerGroup: EventLoopGroup = new NioEventLoopGroup
  ////非线程安全，但是connect是线程安全的，可以共享group
  //connet可以构造channel并从group中取出可用于执行channel的NIO线程
  private[this] lazy final val bootStrap: Bootstrap = new Bootstrap


  @volatile
  private[this] final var channel: Channel = _

  @volatile
  private[this] final var protocol: Protocol = _

  override def open(messageHandler: ClientMessageHandler, socketAddress: SocketAddress, protocol: Protocol): Unit = {
    this.protocol = protocol
    this.channel = ClientChannelBuilder.build(socketAddress, bootStrap, workerGroup, clientChannelInitializer) //使用注入bean
  }

  override def sendMessage(msg: Array[Byte]): Unit = {
    ChannelWriteMessageUtil.sendMsg(channel, msg)
  }

  override def shutdown(): Unit = {
    if (CheckCondition.conditionWarn(channel == null || !channel.isOpen, "channel is already closed")) return
    try {
      channel.close
    } catch {
      case e: Exception =>
        logger.warn("Close NettyClientChannel fail : {}", e)
    }
  }
}
