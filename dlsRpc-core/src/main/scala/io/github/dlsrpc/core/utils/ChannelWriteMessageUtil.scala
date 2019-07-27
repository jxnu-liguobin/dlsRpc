package io.github.dlsrpc.core.utils

import com.typesafe.scalalogging.LazyLogging
import io.github.dlsrpc.common.utils.CheckCondition
import io.netty.channel.{Channel, ChannelFuture}

/**
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
object ChannelWriteMessageUtil extends LazyLogging {

  def sendMsg(outboundChannel: Channel, obj: Any): Unit = {
    outboundChannel.writeAndFlush(obj).addListener((future: ChannelFuture) => {
      if (!CheckCondition.conditionWarn(!future.isSuccess,
        s"OutboundChannel : {$outboundChannel}, sendMsg : {$obj} because {${future.cause}"))
        outboundChannel.read
    })
  }
}
