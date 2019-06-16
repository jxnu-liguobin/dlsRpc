package io.growing.dlsrpc.core.utils

import com.typesafe.scalalogging.LazyLogging
import io.netty.channel.{ChannelFuture, EventLoopGroup}

/**
 * 为channel注册关闭监听
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-16
 */
object NettyListenerUtils extends LazyLogging {

  def addClosedListener(channelFuture: ChannelFuture, bossGroup: EventLoopGroup, workerGroup: EventLoopGroup): Unit = {
    channelFuture.channel().closeFuture().addListener((f: ChannelFuture) => {
      if (bossGroup != null) {
        bossGroup.shutdownGracefully()
      }
      if (workerGroup != null) {
        workerGroup.shutdownGracefully()
      }
      logger.info(f.channel().toString + ",Channel was closed")
    })

    channelFuture.channel()
  }
}
