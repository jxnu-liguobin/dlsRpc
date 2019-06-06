package io.growing.dls.transport.client

import java.net.SocketAddress

import com.typesafe.scalalogging.LazyLogging
import io.growing.dls.exception.RPCException
import io.netty.bootstrap.Bootstrap
import io.netty.channel._
import io.netty.channel.socket.nio.NioSocketChannel

/**
 * 客户端的通道建造器
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
object ClientChannelBuilder extends LazyLogging {

  def build(socketAddress: SocketAddress, workerGroup: EventLoopGroup, channelHandler: ChannelHandler): Channel = {
    lazy val b1 = new Bootstrap
    var channelFuture: ChannelFuture = null
    try {
      channelFuture = b1.group(workerGroup).channel(classOf[NioSocketChannel]).option(ChannelOption.SO_KEEPALIVE,
        Boolean.box(true)).handler(channelHandler).connect(socketAddress).sync.await
      //这里true必须是被封装的
    } catch {
      case e: InterruptedException =>
        logger.warn("Channel  connection time out! socketAddress : {}", socketAddress)
        throw new RPCException(msg = "Channel  connection time out : {} ", e)
    }
    channelFuture.channel
  }
}
