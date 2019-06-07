package io.growing.dls.transport.server

import java.util.concurrent.Executor

import com.typesafe.scalalogging.LazyLogging
import io.growing.dls.SendMessage
import io.growing.dls.server.ServerMessageHandler
import io.growing.dls.utils.{ChannelWriteMessageUtil, IsCondition}
import io.netty.buffer.Unpooled
import io.netty.channel.{Channel, ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.ReferenceCountUtil

/**
 * 服务端消息处理器实现
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
class NettyServerMessageHandler(executor: Executor, messageHandler: ServerMessageHandler)
  extends ChannelInboundHandlerAdapter with SendMessage with LazyLogging {

  private[this] var outboundChannel: Channel = _

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    outboundChannel = ctx.channel
    logger.info("ServerMessageHandlerImpl  init ")
    ctx.read
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = {
    logger.debug("Service read msg : {} ", msg)
    //接收客户端发送的数据
    if (IsCondition.conditionWarn(!msg.isInstanceOf[Array[Byte]], "failure of type matching")) {
      ReferenceCountUtil.release(msg)
      return
    }
    //接收服务端发送的数据
    val writeMsg = msg.asInstanceOf[Array[Byte]]
    //任务异步化
    executor.execute(() => messageHandler.receiveAndProcessor(writeMsg, receiveMessage = this))
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    logger.warn("MessageHandler happen exception : {}", cause)
    if (outboundChannel.isActive) outboundChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE)
  }

  override def sendMsg(msg: Array[Byte]): Unit = {
    ChannelWriteMessageUtil.sendMsg(outboundChannel, msg)
  }
}
