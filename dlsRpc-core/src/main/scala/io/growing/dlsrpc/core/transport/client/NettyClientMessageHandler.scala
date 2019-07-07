package io.growing.dlsrpc.core.transport.client

import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.utils.CheckCondition
import io.growing.dlsrpc.core.client.ClientMessageHandler
import io.netty.buffer.Unpooled
import io.netty.channel.{Channel, ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.ReferenceCountUtil
import javax.inject.{Inject, Singleton}

/**
 * 客户端消息处理实现
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-05
 */
@Singleton
class NettyClientMessageHandler @Inject()(messageHandler: ClientMessageHandler)
  extends ChannelInboundHandlerAdapter with LazyLogging {

  @volatile
  private[this] final var outboundChannel: Channel = _

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    outboundChannel = ctx.channel
    logger.info("NettyClientMessageHandler channelActive")
    ctx.read
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: Any): Unit = {
    logger.debug("Client read  msg : {}", msg)
    CheckCondition.conditionWarn(!msg.isInstanceOf[Array[Byte]], "failure of type matching") match {
      case true => {
        //TODO
      }
      case false => {
        //接收服务端发送的数据
        val writeMsg = msg.asInstanceOf[Array[Byte]]
        //调用io.growing.dls.client.ClientMessageHandler
        messageHandler.receiveProcessor(writeMsg) //使用注入bean
        //如果一个对象实现了ReferenceCounted，并且包含有其他对象也实现了ReferenceCounted，当这个对象计数为0被回收的时候
        //所包含的对象同样会通过release()释放掉。
        ReferenceCountUtil.release(msg)
      }
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    logger.warn("MessageHandler happen exception : {}", cause)
    if (outboundChannel.isActive) outboundChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE)
  }
}
