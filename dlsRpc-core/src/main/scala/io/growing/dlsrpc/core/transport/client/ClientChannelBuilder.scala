package io.growing.dlsrpc.core.transport.client

import java.net.SocketAddress

import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.core.utils.NettyListenerUtils
import io.netty.bootstrap.Bootstrap
import io.netty.channel._
import io.netty.channel.socket.nio.NioSocketChannel

/**
 * 客户端的通道建造器
 *
 * @author 梦境迷离
 * @version 1.2, 2019-06-05
 */
object ClientChannelBuilder extends LazyLogging {

  def build(socketAddress: SocketAddress, bootGroup: Bootstrap, workerGroup: EventLoopGroup, channelHandler: ChannelHandler): Channel = {
    //不再捕获这里的异常
    val channelFuture: ChannelFuture = bootGroup.group(workerGroup).channel(classOf[NioSocketChannel]).
      option(ChannelOption.SO_KEEPALIVE, Boolean.box(true)). //这里true必须是被封装的
      handler(channelHandler).
      connect(socketAddress).
      await()

    NettyListenerUtils.addClosedListener(channelFuture, null, workerGroup,isClient = true)
    logger.info("channelFuture is " + channelFuture)
    channelFuture.channel
  }
}
