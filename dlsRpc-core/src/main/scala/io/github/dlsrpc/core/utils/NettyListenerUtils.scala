package io.github.dlsrpc.core.utils

import com.typesafe.scalalogging.LazyLogging
import io.netty.channel.{ChannelFuture, EventLoopGroup}

/**
 * 为channel注册关闭监听
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-16
 */
object NettyListenerUtils extends LazyLogging {

  /**
   *
   * @param channelFuture
   * @param bossGroup   父线程
   * @param workerGroup 线程
   * @param isClient    是否为客户端
   */
  def addClosedListener(channelFuture: ChannelFuture, bossGroup: EventLoopGroup, workerGroup: EventLoopGroup, isClient: Boolean): Unit = {
    channelFuture.channel().closeFuture().addListener((f: ChannelFuture) => {
      //是客户端时，channel被关闭时不需要再关闭group，因为group是共享的，以防止出现OOM
      if (isClient) {
        f.channel().close()
      } else {
        if (bossGroup != null) {
          bossGroup.shutdownGracefully()
        }
        if (workerGroup != null) {
          workerGroup.shutdownGracefully()
        }
      }
      logger.info(f.channel().toString + ",Channel was closed")
    })

    channelFuture.channel()
  }
}
