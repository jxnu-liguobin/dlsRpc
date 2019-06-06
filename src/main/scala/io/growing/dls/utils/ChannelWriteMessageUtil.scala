package io.growing.dls.utils

import com.typesafe.scalalogging.LazyLogging
import io.netty.channel.{Channel, ChannelFuture}

/**
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
object ChannelWriteMessageUtil extends LazyLogging {

  def sendMsg(outboundChannel: Channel, obj: Any): Unit = {
    outboundChannel.writeAndFlush(obj).addListener((future: ChannelFuture) => {
      if (future.isSuccess) outboundChannel.read
      else logger.warn("OutboundChannel : {}, sendMsg : {}", outboundChannel, obj, future.cause)
    })
  }
}
