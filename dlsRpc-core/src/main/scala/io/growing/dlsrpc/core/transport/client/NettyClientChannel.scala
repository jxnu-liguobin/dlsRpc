package io.growing.dlsrpc.core.transport.client

import java.net.SocketAddress

import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.utils.IsCondition
import io.growing.dlsrpc.core.api.Protocol
import io.growing.dlsrpc.core.client.{ClientChannel, ClientMessageHandler}
import io.growing.dlsrpc.core.utils.ChannelWriteMessageUtil
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

  private[this] lazy final val workerGroup: EventLoopGroup = new NioEventLoopGroup

  @volatile
  private[this] final var channel: Channel = _

  @volatile
  private[this] final var protocol: Protocol = _

  override def open(messageHandler: ClientMessageHandler, socketAddress: SocketAddress, protocol: Protocol): Unit = {
    this.protocol = protocol
    this.channel = ClientChannelBuilder.build(socketAddress, workerGroup, clientChannelInitializer) //使用注入bean
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
    }
  }
}
