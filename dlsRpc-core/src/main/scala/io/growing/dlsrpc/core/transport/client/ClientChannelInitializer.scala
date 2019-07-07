package io.growing.dlsrpc.core.transport.client

import com.google.inject.Singleton
import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.utils.CheckCondition
import io.growing.dlsrpc.core.transport.MessageCodec
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.logging.{LogLevel, LoggingHandler}
import javax.inject.Inject

/**
 * 客户端通道的初始化
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-05
 */
@Singleton
class ClientChannelInitializer @Inject()(nettyClientMessageHandler: NettyClientMessageHandler)
  extends ChannelInitializer[SocketChannel] with LazyLogging {

  @throws[Exception]
  override protected def initChannel(ch: SocketChannel): Unit = {
    CheckCondition.conditionException(ch == null, "ClientChannel can't be null")
    ch.pipeline.addLast("log", new LoggingHandler(LogLevel.INFO))
    ch.pipeline.addLast("messageCodec", new MessageCodec)
    ch.pipeline.addLast("clientMessageHandlerImpl", nettyClientMessageHandler) //使用注入bean
    logger.info("ClientChannelInitializer  initChannel ... ")
  }
}
