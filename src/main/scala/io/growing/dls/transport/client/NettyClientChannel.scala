package io.growing.dls.transport.client

import java.net.SocketAddress

import com.typesafe.scalalogging.LazyLogging
import io.growing.dls.Protocol
import io.growing.dls.client.{ClientChannel, ClientMessageHandler}
import io.growing.dls.utils.{ChannelWriteMessageUtil, IsCondition}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.{Channel, EventLoopGroup}

/**
 * Netty客户端通道
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
class NettyClientChannel extends ClientChannel with LazyLogging {

  private[this] lazy final val workerGroup: EventLoopGroup = new NioEventLoopGroup
  private[this] var channel: Channel = _
  private[this] var protocol: Protocol = _

  override def open(messageHandler: ClientMessageHandler, socketAddress: SocketAddress, protocol: Protocol): Unit = {
    this.protocol = protocol
    this.channel = ClientChannelBuilder.build(socketAddress, workerGroup, new ClientChannelInitializer(messageHandler))
  }

  override def sendMessage(msg: Array[Byte]): Unit = {
    ChannelWriteMessageUtil.sendMsg(channel, msg)
  }

  override def shutdown(): Unit = {
    if (IsCondition.conditionWarn(channel == null || !channel.isOpen, "channel is already closed")) return
    try {
      channel.close
    } catch {
      case e: Exception =>
        logger.warn("Close NettyClientChannel fail : {}", e)
    } finally workerGroup.shutdownGracefully
  }
}
