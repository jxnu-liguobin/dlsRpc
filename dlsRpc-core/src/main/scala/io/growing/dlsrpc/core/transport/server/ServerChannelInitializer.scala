package io.growing.dlsrpc.core.transport.server

import java.util.concurrent.Executor

import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.utils.IsCondition
import io.growing.dlsrpc.core.server.ServerMessageHandler
import io.growing.dlsrpc.core.transport.MessageCodec
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.logging.{LogLevel, LoggingHandler}

/**
 * 服务端通道的初始化
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
class ServerChannelInitializer(executor: Executor, messageHandler: ServerMessageHandler)
  extends ChannelInitializer[SocketChannel] with LazyLogging {

  override def initChannel(c: SocketChannel): Unit = {
    IsCondition.conditionException(c == null, "SocketChannel can't be null")
    c.pipeline.addLast("log", new LoggingHandler(LogLevel.INFO))
    c.pipeline.addLast("messageCodec", new MessageCodec)
    c.pipeline.addLast("server-message-handler", new NettyServerMessageHandler(executor, messageHandler))
    logger.info("ServerChannelInitializer  initChannel ... ")
  }
}
