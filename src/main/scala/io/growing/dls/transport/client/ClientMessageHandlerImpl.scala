package io.growing.dls.transport.client

import com.typesafe.scalalogging.LazyLogging
import io.growing.dls.client.ClientMessageHandler
import io.netty.buffer.Unpooled
import io.netty.channel.{Channel, ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.ReferenceCountUtil

/**
 * 客户端消息处理实现
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
class ClientMessageHandlerImpl(messageHandler: ClientMessageHandler)
  extends ChannelInboundHandlerAdapter with LazyLogging {

  private[this] var outboundChannel: Channel = _

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    outboundChannel = ctx.channel
    logger.info("ClientMessageHandlerImpl  init ")
    ctx.read
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = {
    logger.debug("Client read  msg : {}", msg)
    if (!msg.isInstanceOf[Array[Byte]]) {
      ReferenceCountUtil.release(msg)
      return
    }
    //接收服务端发送的数据
    val writeMsg = msg.asInstanceOf[Array[Byte]]
    //调用io.growing.dls.client.ClientMessageHandler
    messageHandler.receiveAndProcessor(writeMsg)
    ReferenceCountUtil.release(msg)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    logger.warn("MessageHandler happen exception : {}", cause)
    if (outboundChannel.isActive) outboundChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE)
  }
}
