package io.growing.dls.transport.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.{ChannelFuture, ChannelHandler, ChannelOption, EventLoopGroup}

/**
 * 服务端建造器
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
object ServerChannelBuilder {

  def build(bossGroup: EventLoopGroup, workerGroup: EventLoopGroup,
            channelHandler: ChannelHandler, port: Int): ChannelFuture = {
    val b = new ServerBootstrap
    b.group(bossGroup, workerGroup).channel(classOf[NioServerSocketChannel]).childHandler(channelHandler).
      option(ChannelOption.SO_KEEPALIVE, Boolean.box(true)).bind(port)
    //需要封装true，不然推断出错，接收参数是Option，会被推断为Option[Any]
  }
}
