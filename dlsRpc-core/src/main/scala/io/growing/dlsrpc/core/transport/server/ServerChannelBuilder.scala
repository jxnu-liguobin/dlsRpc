package io.growing.dlsrpc.core.transport.server

import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.core.utils.NettyListenerUtils
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel._
import io.netty.channel.socket.nio.NioServerSocketChannel

/**
 * 服务端建造器
 *
 * @author 梦境迷离
 * @version 1.3, 2019-06-05
 */
object ServerChannelBuilder extends LazyLogging {

  def build(bossGroup: EventLoopGroup, workerGroup: EventLoopGroup,
            channelHandler: ChannelHandler, port: Int): Channel = {
    lazy val boot = new ServerBootstrap
    val channelFuture: ChannelFuture = boot.group(bossGroup, workerGroup).
      channel(classOf[NioServerSocketChannel]).
      childHandler(channelHandler).
      option(ChannelOption.SO_KEEPALIVE, Boolean.box(true)). //需要封装true，不然推断出错，接收参数是Option，会被推断为Option[Any]
      bind(port).await()

    NettyListenerUtils.addClosedListener(channelFuture, bossGroup, workerGroup, isClient = false)

    channelFuture.channel()
  }

}
