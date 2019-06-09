package io.growing.dlsrpc.core.transport.client

import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.utils.IsCondition
import io.growing.dlsrpc.core.client.ClientMessageHandler
import io.growing.dlsrpc.core.transport.MessageCodec
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.logging.{LogLevel, LoggingHandler}

/**
 * 客户端通道的初始化
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
class ClientChannelInitializer(clientMessageHandler: ClientMessageHandler)
  extends ChannelInitializer[SocketChannel] with LazyLogging {

  @throws[Exception]
  override protected def initChannel(ch: SocketChannel): Unit = {
    IsCondition.conditionException(ch == null, "ClientChannel can't be null")
    ch.pipeline.addLast("log", new LoggingHandler(LogLevel.INFO))
    ch.pipeline.addLast("messageCodec", new MessageCodec)
    ch.pipeline.addLast("clientMessageHandlerImpl", new NettyClientMessageHandler(clientMessageHandler))
    logger.info("ClientChannelInitializer  initChannel ... ")
  }
}
